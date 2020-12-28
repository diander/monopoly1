<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as l>

<@c.page true>

<#if Session?? && Session.SPRING_SECURITY_LAST_EXCEPTION??>
    <div class="alert alert-danger" role="alert">
        ${Session.SPRING_SECURITY_LAST_EXCEPTION.message}
    </div>
</#if>

<#if message??>
    <div class="alert alert-${messageType}" role="alert">
        ${message}
    </div>
</#if>

<div class="wrapper">
    <h1 class="login-sign">Login</h1>
    <@l.login "/login" false />
</div>

</@c.page>