<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hubbub&trade; &raquo; Join</title>
        <style>
            .flash {color:red;}
        </style>
    </head>
    <body>
        <p><center><img src="images/hubbub_logo.png"/></center></p>
        <h1>Join Our Community</h1>
        <a href="main?action=login">I'm a Member</a> |
        <a href="main?action=timeline">Back to the Timeline</a>
        <c:if test="${not empty flash}">
            <h2 class="flash">${flash}</h2>
        </c:if>
        <form method="POST" action="main">
            <input type="hidden" name="action" value="join"/>
            <table>
                <tr>
                    <td>Choose a Username:</td>
                    <td><input type="text" name="username" required
                               placeholder="6-12 characters"/></td>
                </tr>
                <tr>
                    <td>Pick a Strong Password:</td>
                    <td><input type="password" name="password1" required
                               placeholder="8-20 characters"/></td>
                </tr>
                <tr>
                    <td>Repeat That Password:</td>
                    <td><input type="password" name="password2" required
                               placeholder="same as above"/></td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="submit" value="Let's Get Hackin'"/>
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>
