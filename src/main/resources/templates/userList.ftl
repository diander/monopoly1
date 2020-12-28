<#import "parts/common.ftl" as c>
<#include "parts/security.ftl">

<@c.page false>

<h5>Rating table</h5>

<table class="table my-3">
    <thead>
    <tr>
        <th scope="col">Name</th>
        <th scope="col">Wins</th>
        <th scope="col">Defeats</th>
    </tr>
    </thead>
    <tbody>
        <#list users as tempUser>
            <tr class="table-row" data-href="">
                <th scope="row">
                    <a href="/user/${tempUser.id}" >
                        ${tempUser.username?html}
                    </a>
                </th>
                <td>
                    ${tempUser.wins?html}
                </td>
                <td>
                    ${tempUser.defeats?html}
                </td>
            </tr>

        </#list>
    </tbody>
</table>



</@c.page>