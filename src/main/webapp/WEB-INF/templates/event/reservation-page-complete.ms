{{>/event/page-top}}

<div>
	{{#event.imageIsPresent}}
        <img src="{{event.imageUrl}}" alt="{{event.shortName}}" class="img-responsive img-center">
	{{/event.imageIsPresent}}
</div>

{{^ticketsAreAllAssigned}}
<ol class="breadcrumb wMarginTop">
    <li class="active">{{#i18n}}breadcrumb.step1{{/i18n}}</li>
    <li class="active">{{#i18n}}breadcrumb.step2{{/i18n}}</li>
    <li><i class="fa fa-download"></i> {{#i18n}}breadcrumb.step3{{/i18n}}</li>
</ol>
{{/ticketsAreAllAssigned}}

<div class="wMarginBottom"></div>

<h1>
	{{#ticketsAreAllAssigned}}
		{{#i18n}}reservation-page-complete.your-tickets [{{event.shortName}}]{{/i18n}} 
	{{/ticketsAreAllAssigned}}
	{{^ticketsAreAllAssigned}}
		{{#i18n}}reservation-page-complete.assign-your-tickets [{{event.shortName}}]{{/i18n}}
	{{/ticketsAreAllAssigned}}
</h1>

{{#confirmationEmailSent}}
<div class="alert alert-success alert-dismissible" role="alert">
  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
  <strong>{{#i18n}}email.confirmation-email-sent{{/i18n}}</strong>
</div>
{{/confirmationEmailSent}}
{{#ticketEmailSent}}
<div class="alert alert-success alert-dismissible" role="alert">
  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
  <strong>{{#i18n}}email.ticket-email-sent{{/i18n}}</strong>
</div>
{{/ticketEmailSent}}

{{#ticketsAreAllAssigned}}
<p class="bg-info ticket-info">{{#i18n}}reservation-page-complete.info-update{{/i18n}}</p>
{{/ticketsAreAllAssigned}}

{{^ticketsAreAllAssigned}}
<div class="alert alert-success">
	<div class="row">
		<div class="col-sm-12">
			{{#i18n}}reservation-page-complete.info-assign [{{event.shortName}}]{{/i18n}}
		</div>
	</div>
    <div class="row">
        <div class="col-sm-12">
			{{#i18n}}reservation-page-complete.info-assign-email [{{reservation.email}}]{{/i18n}}
        </div>
    </div>
	<div class="row">
		<div class="col-sm-4 col-sm-offset-8">
		<form action="{{request.contextPath}}/event/{{event.shortName}}/reservation/{{reservationId}}/re-send-email" method="post" class="form-inline">
			<button type="submit" class="btn btn-default">{{#i18n}}reservation-page-complete.resend-reservation-email{{/i18n}}</button>
			<input type="hidden" name="{{_csrf.parameterName}}" value="{{_csrf.token}}">
		</form>
		</div>
	</div>
</div>
{{/ticketsAreAllAssigned}}

<ul class="list-group">
{{#ticketsByCategory}}
	{{#value}}
	<li class="list-group-item">
			<div class="wMarginBottom">
				<h3><i class="fa fa-ticket"></i> {{#i18n}}reservation-page-complete.ticket-nr{{/i18n}}<span class="ticket-counter"></span></h3>
			</div>
			{{#i18n}}reservation-page-complete.ticket-type{{/i18n}} <strong>{{key.name}}</strong>
			<div id="ticket-detail-{{uuid}}">
				{{> /event/assign-ticket-form}}
			</div>
	</li>
	{{/value}}
{{/ticketsByCategory}}
</ul>
<div class="container-fluid hidden" id="back-to-event-site">
	<div class="row">
		<div class="col-md-4 col-md-push-8 col-xs-12 wMarginBottom"><a href="{{request.contextPath}}/event/{{event.shortName}}/" class="form-control-static btn btn-success btn-block">{{#i18n}}buy-other-tickets{{/i18n}}</a></div>
		<div class="col-md-4 col-md-pull-4 col-xs-12"><a href="{{event.websiteUrl}}" class="form-control-static btn btn-default btn-block">{{#i18n}}to-event-site{{/i18n}}</a></div>
	</div>
</div>
<div class="clearfix wMarginBottom"></div>
<div class="text-center">
	{{#i18n}}reservation-page-complete.order-information [{{reservation.id}}] [{{reservation.fullName}}]{{/i18n}}
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="{{request.contextPath}}/resources/js/event/reservation-page-complete.js"></script>
	
{{>/event/page-bottom}}