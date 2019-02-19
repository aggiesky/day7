<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hubbub&trade; &raquo; Timeline</title>
        <style>
            .flash {color:red;}
        </style>
    </head>
    <body>
    	<p><center><img src="images/hubbub_logo.png"/></center></p>
        <h1>
            Welcome to Hubbub&trade;<c:if test="${not empty user}">, ${user}</c:if>!
        </h1>
        <h2 class="flash">${flash}</h2>
        <c:choose>
            <c:when test="${not empty user}">
                <a href="main?action=post">Post Yer Hack Status</a> |
                <a href="main?action=wall&for=${user}">Show Me My Wall</a> |
                <a href="main?action=profile&for=${user}">View My Profile</a> |
                <a href="main?action=logout">Log Me Out</a>
            </c:when>
            <c:otherwise>
                <a href="main?action=login">Log Me In!</a> |
                <a href="main?action=join">Sign Me Up!</a>
            </c:otherwise>
        </c:choose>
        <c:choose>
            <c:when test="${empty param.for}">
                <h2>Here's what our users are hackin' on:</h2>
            </c:when>
            <c:otherwise>
                | <a href="main?action=timeline">Back to the Timeline</a>
                <h2>
                    Here's what
                    <a href="main?action=profile&for=${param.for}">
                        ${param.for}
                    </a> is hackin' on:
                </h2>
            </c:otherwise>
        </c:choose>
        <ul>
        <c:forEach var="post" items="${posts}">
            <li>
                Posted by
                <span class="author" title="${post.author.joinDate}">
                    <c:choose>
                        <c:when test="${not empty user}">
                            <a href="main?action=wall&for=${post.author}">
                                ${post.author}
                            </a>
                        </c:when>
                        <c:otherwise>
                            ${post.author}
                        </c:otherwise>
                    </c:choose>
                </span>
                <span class="postdate"><f:formatDate type="both" value="${post.postDate}"/></span>
                <p class="content">${post.content}</p>
            </li>
        </c:forEach>
        </ul>
        <c:if test="${empty posts}">
        <h3>Nothin'. Absolutely Nothin'.</h3>
        </c:if>
        
        <c:if test="${not empty user}">
        <c:choose>
        <c:when test="${not empty param.for}">
        <c:if test="${pager.page gt 0}">
        <a href="main?action=wall&for=${param.for}&page=0">Start</a>&nbsp;&nbsp;&nbsp;
        </c:if>
        <c:if test="${pager.hasNext eq true}">
        <a href="main?action=wall&for=${param.for}&page=${pager.next}">Next Page</a>&nbsp;&nbsp;&nbsp;
        </c:if>
        <c:if test="${pager.hasPrev}">
        <a href="main?action=wall&for=${param.for}&page=${pager.prev}">Previous Page</a>&nbsp;&nbsp;&nbsp;
        </c:if>
        <c:if test="${pager.hasNext eq true}">
        <a href="main?action=wall&for=${param.for}&page=${pager.last}">End</a>
        </c:if>
        </c:when>
        <c:otherwise>
        <c:if test="${pager.page gt 0}">
        <a href="main?action=timeline&page=0">Start</a>&nbsp;&nbsp;&nbsp;
        </c:if>
        <c:if test="${pager.hasNext eq true}">
        <a href="main?action=timeline&page=${pager.next}">Next Page</a>&nbsp;&nbsp;&nbsp;
        </c:if>
        <c:if test="${pager.hasPrev}">
        <a href="main?action=timeline&page=${pager.prev}">Previous Page</a>&nbsp;&nbsp;&nbsp;
        </c:if>
        <c:if test="${pager.hasNext eq true}">
        <a href="main?action=timeline&page=${pager.last}">End</a>
        </c:if>            
        </c:otherwise>
        </c:choose>
        </c:if>
    </body>
</html>
