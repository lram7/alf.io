/**
 * This file is part of alf.io.
 *
 * alf.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * alf.io is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with alf.io.  If not, see <http://www.gnu.org/licenses/>.
 */
package alfio.manager.system;

import alfio.config.Initializer;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@Profile(Initializer.PROFILE_DEV)
public class MockMailer implements Mailer {

	@Override
	public void send(String to, String subject, String text, Optional<String> html, Attachment... attachments) {
		log.info("Email: to: {}, subject: {}, text: {}, html: {}, attachments amount: {}", to, subject, text,
				html.orElse("no html"), ArrayUtils.getLength(attachments));
	}
}