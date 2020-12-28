<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as l>

<@c.page true>

<div class="mb-1"><h5>Register</h5></div>
${message?ifExists}
<@l.login "/registration" true />

</@c.page>