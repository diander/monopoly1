<#include "security.ftl">

<#macro login path isRegisterForm>
<div>
    <form action="${path}" method="post"  class="login-form">

        <div class="form-group row">
            <div class="col-sm-6">
                <label class="col-sm-2 col-form-label" for="usernameInput">Username:</label>
                <input id="usernameInput" type="text" name="username" value="<#if user??>${user.username?ifExists?html}</#if>"
                       class="form-control ${(usernameError??)?string('is-invalid', '')}"
                       placeholder="User name"/>
                <#if usernameError??>
                    <div class="invalid-feedback">
                        ${usernameError}
                    </div>
                </#if>
            </div>
        </div>

        <div class="form-group row">
            <div class="col-sm-6">
                <label class="col-sm-2 col-form-label" for="passwordInput">Password:</label>
                <input id="passwordInput" type="password" name="password"
                       class="form-control ${(passwordError??)?string('is-invalid', '')}"
                       placeholder="Password"/>
                <#if passwordError??>
                    <div class="invalid-feedback">
                        ${passwordError}
                    </div>
                </#if>
            </div>
        </div>

        <#if isRegisterForm>
            <div class="form-group row">
                <div class="col-sm-6">
                    <label class="col-sm-2 col-form-label" for="password2Input">Confirm password:</label>
                    <input id="password2Input" type="password" name="password2"
                           class="form-control ${(password2Error??)?string('is-invalid', '')}"
                           placeholder="Password"/>
                    <#if password2Error??>
                        <div class="invalid-feedback">
                            ${password2Error}
                        </div>
                    </#if>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-6">
                    <label class="col-sm-2 col-form-label" for="emailInput">Email:</label>
                    <input id="emailInput" type="email" name="email" value="<#if user??>${user.email?ifExists?html}</#if>"
                           class="form-control ${(emailError??)?string('is-invalid', '')}"
                           placeholder="some@some.com"/>
                    <#if emailError??>
                        <div class="invalid-feedback">
                            ${emailError}
                        </div>
                    </#if>
                </div>
            </div>

        </#if>

        <input type="hidden" name="_csrf" value="${_csrf.token}" />

        <div class="form-group row">
            <div class="col-sm-6">
                <button class="btn btn-secondary" type="submit"><#if isRegisterForm>Sign up<#else>Sign in</#if></button>
            </div>
        </div>

        <#if !isRegisterForm>
            <div class="form-group row">
                <div class="col-sm-6">
                    <a class="btn btn-secondary" href="/registration">Register</a>
                </div>
            </div>
        </#if>

    </form>
</div>
</#macro>

<#macro logout>
<form action="/logout" method="post">
    <input type="hidden" name="_csrf" value="${_csrf.token}" />
    <button class="btn btn-secondary" type="submit"><#if user?? && known>Log Out<#else>Log in</#if></button>
</form>
</#macro>