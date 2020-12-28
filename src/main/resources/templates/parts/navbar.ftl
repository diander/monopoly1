<#include "security.ftl">
<#import "login.ftl" as l>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <img src="/static/img/favicon.ico" width="50px" height="50px" />
    <a class="navbar-brand ml-2" href="/restroom">Monopoly</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">

            <#if user??>

                <li class="nav-item">
                    <a class="nav-link" href="/">Play</a>
                </li>


                <li class="nav-item">
                    <a class="nav-link" href="/rating">Rating</a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" href="/user/${id}">Profile</a>
                </li>

            </#if>

        </ul>

        <#if user??>
            <#if !activated>
                <div class="navbar-text mr-3 alert-danger">Please, activate your account</div>
            </#if>
        </#if>

        <div class="navbar-text mr-3">
            <#if username??>
            You are logged in as: <a href="/user/${id}">${username?html}</a>
            <#else>Please, log in
            </#if>
        </div>

        <@l.logout />

    </div>
</nav>