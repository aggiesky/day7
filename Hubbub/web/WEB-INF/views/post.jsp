<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hubbub&trade; &raquo; Post</title>
        <style>.flash {color:red;}</style>
    </head>
    <body>
        <p><center><img src="images/hubbub_logo.png"/></center></p>
        <a href="main?action=timeline">Back to the Timeline</a> |
        <a href="main?action=wall">Go to My Wall</a> |
        <a href="main?action=logout">Log Me Out</a>
        <h1>What Are You Hackin' on Today?</h1>
        <h2 class="flash">${flash}</h2>

        <form method="POST" action="main">
            <input type="hidden" name="action" value="post"/>
            <table>
                <tr>
                    <td>
            <textarea cols="50" rows="6" name="content">${content}</textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><input type="submit" value="Tell the World"/></td>
                </tr>
            </table>
        </form>
        <c:if test="${not empty lastPost}">
        <ul>
            <li>
                <span class="postdate">${lastPost.postDate}</span>
                <p class="content">${lastPost.content}</p>
            </li>
        </ul>
        </c:if>
    </body>
</html>
