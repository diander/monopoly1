<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as l>

<@c.page false>

<h5>An error occured!</h5><br/>

<#if Session?? && Session.SPRING_SECURITY_LAST_EXCEPTION??>
    <div class="alert alert-danger" role="alert">
        ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}
    </div>
</#if>

<#if alert??>
    <div class="alert alert-${messageType}" role="alert">
        ${alert}
    </div>
</#if>

</@c.page>