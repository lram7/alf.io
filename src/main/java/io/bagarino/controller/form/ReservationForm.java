/**
 * This file is part of bagarino.
 *
 * bagarino is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * bagarino is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with bagarino.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.bagarino.controller.form;

import static io.bagarino.util.OptionalWrapper.optionally;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import io.bagarino.controller.ErrorsCode;
import io.bagarino.controller.decorator.SaleableTicketCategory;
import io.bagarino.manager.EventManager;
import io.bagarino.manager.TicketReservationManager;
import io.bagarino.model.Event;
import io.bagarino.model.SpecialPrice;
import io.bagarino.model.SpecialPrice.Status;
import io.bagarino.model.TicketCategory;
import io.bagarino.model.modification.TicketReservationModification;
import io.bagarino.model.modification.TicketReservationWithOptionalCodeModification;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

//step 1 : choose tickets
@Data
public class ReservationForm {

	private String promoCode;
	private List<TicketReservationModification> reservation;

	private List<TicketReservationModification> selected() {
		return ofNullable(reservation)
				.orElse(emptyList())
				.stream()
				.filter((e) -> e != null && e.getAmount() != null && e.getTicketCategoryId() != null
						&& e.getAmount() > 0).collect(toList());
	}

	private int selectionCount() {
		return selected().stream().mapToInt(TicketReservationModification::getAmount).sum();
	}

	public Optional<List<TicketReservationWithOptionalCodeModification>> validate(BindingResult bindingResult,
			TicketReservationManager tickReservationManager,
			EventManager eventManager,
			Event event) {
		int selectionCount = selectionCount();

		if (selectionCount <= 0) {
			bindingResult.reject(ErrorsCode.STEP_1_SELECT_AT_LEAST_ONE);
			return Optional.empty();
		}

		final int maxAmountOfTicket = tickReservationManager.maxAmountOfTickets();

		if (selectionCount > maxAmountOfTicket) {
			bindingResult.reject(ErrorsCode.STEP_1_OVER_MAXIMUM, new Object[] { maxAmountOfTicket }, null);
		}

		final List<TicketReservationModification> selected = selected();
		final ZoneId eventZoneId = selected.stream().findFirst().map(r -> {
			TicketCategory tc = eventManager.getTicketCategoryById(r.getTicketCategoryId(), event.getId());
			return eventManager.findEventByTicketCategory(tc).getZoneId();
		}).orElseThrow(IllegalStateException::new);

		List<TicketReservationWithOptionalCodeModification> res = new ArrayList<>();
		//
		Optional<SpecialPrice> specialCode = Optional.ofNullable(StringUtils.trimToNull(promoCode)).flatMap(
				(trimmedCode) -> optionally(() -> tickReservationManager.getSpecialPriceByCode(trimmedCode)));
		//
		final ZonedDateTime now = ZonedDateTime.now(eventZoneId);
		selected.forEach((r) -> {

			TicketCategory tc = eventManager.getTicketCategoryById(r.getTicketCategoryId(), event.getId());
			SaleableTicketCategory ticketCategory = new SaleableTicketCategory(tc, now, event, tickReservationManager
					.countUnsoldTicket(event.getId(), tc.getId()), maxAmountOfTicket);

			if (!ticketCategory.getSaleable()) {
				bindingResult.reject(ErrorsCode.STEP_1_TICKET_CATEGORY_MUST_BE_SALEABLE); //
			}

			boolean canAccessRestrictedCategory = specialCode.isPresent()
					&& specialCode.get().getStatus() == Status.FREE
					&& specialCode.get().getTicketCategoryId() == ticketCategory.getId();
			if (canAccessRestrictedCategory && r.getAmount().intValue() > 1) {
				bindingResult.reject(ErrorsCode.STEP_1_OVER_MAXIMUM_FOR_RESTRICTED_CATEGORY,
						new Object[] { 1, tc.getName() }, null);
			}

			if (!canAccessRestrictedCategory && ticketCategory.isAccessRestricted()) {
				bindingResult.reject(ErrorsCode.STEP_1_ACCESS_RESTRICTED); //
			}

			res.add(new TicketReservationWithOptionalCodeModification(r, canAccessRestrictedCategory
					&& r.getAmount().intValue() == 1 ? specialCode : Optional.empty()));
		});

		return bindingResult.hasErrors() ? Optional.empty() : Optional.of(res);
	}
}