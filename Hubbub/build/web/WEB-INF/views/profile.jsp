<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hubbub&trade; &raquo; Profile &raquo; ${param.for}</title>
        <style>
            .flash {color:red;}
            .success {color:blue;}
        </style>
    </head>
    <body>
        <p><center><img src="images/hubbub_logo.png"/></center></p>
        <a href="main?action=timeline">The Timeline</a> |
        <a href="main?action=wall&for=${param.for}">${param.for}'s Wall</a>
        <a href="main?action=wall&for=${user.username}">My Wall</a> |
        <a href="main?action=logout">Log Out</a>
        <h1>
            Hubbub&trade; Profile for ${param.for} (user since 
            <c:choose>
            <c:when test="${not empty target}">${target.joinDate}</c:when>
            <c:otherwise>${user.joinDate}</c:otherwise>
            </c:choose>
            )
        </h1>
        <c:choose>
        <c:when test="${not empty flash}">
        <h2 class="flash">${flash}</h2>
        </c:when>
        <c:when test="${not empty success}">
        <h2 class="success">${success}</h2>
        </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${not empty profile.avatar}">
        <img src="avatar?for=${user.username}"/>
            </c:when>
            <c:otherwise>
        <img src="images/domo.jpg"/>
            </c:otherwise>
        </c:choose>
        <form method="POST" action="main">
            <input type="hidden" name="action" value="profile"/>
            <input type="hidden" name="for" value="${param.for}"/>
            <c:set var="disabled" value="${param.for eq user.username ? '' : 'disabled'}"/>
            <c:if test="${empty disabled}">
            <p>The following fields are all optional and may be updated at any time.</p>
            </c:if>
            <table>
                <tr>
                    <td>First Name:</td>
                    <td><input type="text" name="firstName" value="${profile.firstName}"
                               placeholder="&lt;50 letters" ${disabled}/></td>
                </tr>
                <tr>
                    <td>Last Name:</td>
                    <td><input type="text" name="lastName" value="${profile.lastName}"
                               placeholder="&lt;50 letters" ${disabled}/></td>
                </tr>
                <tr>
                    <td>Email Address:</td>
                    <td><input type="type" name="emailAddress" value="${profile.emailAddress}" ${disabled}/></td>
                </tr>
                <tr>
                    <td>Biography (255 characters or less):</td>
                    <td><textarea rows="10" cols="50" name="biography" ${disabled}>${profile.biography}</textarea></td>
                </tr>
                <c:if test="${empty disabled}">
                <tr>
                    <td colspan="2"><input type="submit" value="Save Changes"/></td>
                </tr>
                </c:if>
            </table>
        </form>
    </body>
</html>
