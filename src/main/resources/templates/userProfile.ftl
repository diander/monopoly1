<#import "parts/common.ftl" as c>
<#include "parts/security.ftl">

<@c.page false>

<table class="border0">
    <tr>
        <td>
            <div class="container my-3">
                <h3>${targetUser.username?html}<h3>
            </div>
        </td>
    </tr>
    <tr>
        <td>
            <table class="table">
                <thead>
                    <tr>
                        <th scope="col">Wins</th>
                        <th scope="col">Defeats</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>${targetUser.wins?html}</td>
                        <td>${targetUser.defeats?html}</td>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
</table>

</@c.page>