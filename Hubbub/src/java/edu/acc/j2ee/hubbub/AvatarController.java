package edu.acc.j2ee.hubbub;

import edu.acc.j2ee.hubbub.domain.HubbubDao;
import edu.acc.j2ee.hubbub.domain.Profile;
import edu.acc.j2ee.hubbub.domain.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AvatarController extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String subject = request.getParameter("for");
        if (subject == null || subject.length()== 0)
            subject = ((User)request.getSession().getAttribute("user")).getUsername();
        try {
            HubbubDao dao = (HubbubDao)this.getServletContext().getAttribute("dao");
            User of = dao.findUserByUsername(subject);
            Profile profile = dao.findProfileByUser(of);
            String mime = profile.getMime();
            byte[] avatar = profile.getAvatar();
            if (avatar == null || mime == null) {
                response.sendRedirect("images/domo.jpg");
                return;
            }
            response.setContentType(mime);
            response.getOutputStream().write(avatar);
        } catch (IOException ioe) {
            response.sendRedirect("images/domo.jpg");
        }

    }
}
