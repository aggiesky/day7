<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hubbub&trade; &raquo; Profile &raquo; ${target.username}</title>
        <style>
            .flash {color:red;}
            .success {color:blue;}
        </style>
    </head>
    <body>
        <p><center><img src="images/hubbub_logo.png"/></center></p>
        
<!-- LINKS AREA CUSTOMIZED BY WHO THE TARGET IS RELATIVE TO THE USER -->
        <a href="main?action=timeline">The Timeline</a> |
        <a href="main?action=wall&for=${target.username}">${target.username}'s Wall</a>
        <c:if test="${target.username ne user.username}">
            <a href="main?action=wall&for=${user.username}">My Wall</a>
        </c:if> |
        <a href="main?action=logout">Log Out</a>
        <h1>
            Hubbub&trade; Profile for ${target.username} (user since ${target.profile.joinDate})
        </h1>
        
        <!-- AREA TO DISPLAY FLASH OR PRFOILE UPDATED MESSAGE -->
        <c:choose>
        <c:when test="${not empty flash}">
        <h2 class="flash">${flash}</h2>
        </c:when>
        <c:when test="${not empty success}">
        <h2 class="success">${success}</h2>
        </c:when>
        </c:choose>
        
        <img src="avatar?for=${target.username}"/>

        <c:if test="${target.username eq user.username}">
            <a href="main?action=avatar">Upload a new avatar</a>
            <c:if test="${user.profile.avatar ne null}">
            | <a href="main?action=revert">Revert avatar to default</a>
            </c:if>
        </c:if>
            
        <form method="POST" action="main">
            <input type="hidden" name="action" value="profile"/>
            <input type="hidden" name="for" value="${target.username}"/>
            <c:set var="disabled" value="${target.username eq user.username ? '' : 'disabled'}"/>
            <c:if test="${empty disabled}">
            <p>The following fields are all optional and may be updated at any time.</p>
            </c:if>
            <table>
                <tr>
                    <td>First Name:</td>
                    <td><input type="text" name="firstName" value="${target.profile.firstName}"
                               placeholder="&lt;50 letters" ${disabled}/></td>
                </tr>
                <tr>
                    <td>Last Name:</td>
                    <td><input type="text" name="lastName" value="${target.profile.lastName}"
                               placeholder="&lt;50 letters" ${disabled}/></td>
                </tr>
                <tr>
                    <td>Email Address:</td>
                    <td><input type="type" name="emailAddress" value="${target.profile.emailAddress}" ${disabled}/></td>
                </tr>
                <tr>
                    <td>Biography (255 characters or less):</td>
                    <td><textarea rows="10" cols="50" name="biography" ${disabled}>${target.profile.biography}</textarea></td>
                </tr>
                <c:if test="${empty disabled}">
                <tr>
                    <td colspan="2"><input type="submit" value="Save Changes"/></td>
                </tr>
                </c:if>
            </table>
        </form>
        <c:if test="${target.username eq user.username}">
        <h3>Currently Following:</h3>
        <ul>
        <c:forEach var="followee" items="${user.profile.followees}">
            <li><a href="main?action=profile&for=${followee}">${followee}</a></li>
        </c:forEach>
        </ul>
        <h3>Currently Being Followed By:</h3>
        <ul>
        <c:forEach var="follower" items="${user.profile.followers}">
            <li><a href="main?action=profile&for=${follower}">${follower}</a></li>
        </c:forEach>
        </ul>
        </c:if>
        <c:if test="${user.profile.followees.contains(target.username)}">
            <a href="main?action=unfollow&target=${target.username}">Unfollow ${target.username}</a>
        </c:if>
    </body>
</html>
