<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hubbub&trade; &raquo; Avatar Upload</title>
        <style>
            .flash {color:red;}
        </style>
    </head>
    <body>
        <img src="images/hubbub_logo.png"/><br/>
        <h1>Avatar of <a href="main?action=wall&for=${user.username}">${user.username}</a></h1>
        <h2 class="flash">${flash}</h2>
        <p> <a href="main">Back to the Timeline</a> |
            <a href="main?action=profile&for=${user.username}">Back to ${user.username}'s Profile</a> |
            <a href="main?action=wall&for=${user.username}">Back to ${user.username}'s Wall</a> |
            <a href="main?action=post">Post Something</a> |
            <a href="main?action=logout">Log me out</a>
        <p>
        <form method="POST" action="main" enctype="multipart/form-data">
            <input type="hidden" name="action" value="avatar"/>
            <table id="formtable">
                <tr>
                    <td><label for="mime">Use yer chooser:</label></td>
                    <td>Current Avatar:</td>
                </tr>
                <tr>
                    <td><input type="file" name="avatar" id="avatar"/></td>
                    <td rowspan="2"><img src="avatar?for=${user.username}"/></td>
                </tr>
                <tr><td><input type="submit" value="Upload that puppy!"/></td></tr>
            </table>
        </form>           
    </body>
</html>