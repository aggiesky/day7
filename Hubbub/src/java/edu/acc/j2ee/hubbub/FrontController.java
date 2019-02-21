package edu.acc.j2ee.hubbub;

import edu.acc.j2ee.hubbub.domain.HubbubDao;
import edu.acc.j2ee.hubbub.domain.User;
import edu.acc.j2ee.hubbub.domain.Post;
import edu.acc.j2ee.hubbub.domain.Profile;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class FrontController extends HttpServlet {
    private HubbubDao dao;

    @Override
    public void init() {
        dao = this.getDao();
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action"), destination;
        if (action == null || action.length() == 0)
            action = this.getServletConfig().getInitParameter("default.action");
        switch (action) {
            default:
            case "wall": destination = wall(request); break;
            case "timeline": destination = timeline(request); break;
            case "login": destination = login(request); break;
            case "logout": destination = logout(request); break;
            case "join": destination = join(request); break;
            case "post": destination = post(request); break;
            case "profile": destination = profile(request); break;
            case "avatar": destination = avatar(request); break;
            case "revert": destination = revert(request); break;
            case "follow": destination = follow(request); break;
            case "unfollow": destination = unfollow(request); break;
        }
        
        String redirect = this.getServletConfig().getInitParameter("redirect.tag");
        if (destination.startsWith(redirect)) {
            response.sendRedirect("main?action=" + destination.substring(
                destination.indexOf(redirect) + redirect.length()));
            return;
        }
        String viewDir = this.getServletConfig().getInitParameter("view.dir");
        String viewType = this.getServletConfig().getInitParameter("view.type");
        request.getRequestDispatcher(viewDir + destination + viewType)
                .forward(request, response);
    }
    
    private String login(HttpServletRequest request) {
        if (loggedIn(request)) return "redirect:timeline";
        if (isGet(request)) return "login";             
        try {
            User maybe = new User(request.getParameter("username"), request.getParameter("password"));
            User user = dao.findUserByUsernameAndPassword(maybe.getUsername(),maybe.getPassword());
            if (user == null) {
                request.setAttribute("flash", "Access Denied");
                return "login";
            }
            request.getSession().setAttribute("user", user);
            return "redirect:timeline";
        }
        catch (IllegalArgumentException iae) {
            request.setAttribute("flash", "Username or password is invalid");
            return "login";
        }
        catch (SQLException sqle) {
            request.setAttribute("flash", sqle.getMessage());
            return "login";
        }
    }
    
    private String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:timeline";
    }
    
    private String timeline(HttpServletRequest request) {
        List<Post> posts;
        try {
            Pager pager = Pager.of(request.getParameter("page"), getTimelinePageSize(),
                dao.countAllPosts());
            posts = dao.findPostsInRange(pager.getPage(), pager.getPageSize());
            request.setAttribute("pager", pager);
            request.setAttribute("posts", posts);
        }
        catch (SQLException sqle) {
            request.setAttribute("flash", sqle.getMessage());
        }
        return "timeline";
    }
    
    private String wall(HttpServletRequest request) {
        try {
            User whoseWall = request.getParameter("for") != null ?
                    dao.findUserByUsername(request.getParameter("for")) :
                    this.getSessionUser(request);
            Pager pager = Pager.of(request.getParameter("page"), getTimelinePageSize(),
                    dao.countUserPosts(whoseWall));
            List<Post> posts = dao.findUserPostsInRange(whoseWall, pager.getPage(),
                    pager.getPageSize());
            pager.setSize(dao.countUserPosts(whoseWall));
            request.setAttribute("posts", posts);
            request.setAttribute("pager", pager);
        }
        catch (SQLException sqle) {
            request.setAttribute("flash", sqle.getMessage());
        }
        return "timeline";
    }
    
    private String join(HttpServletRequest request) {
        if (loggedIn(request)) return "redirect:timeline";
        if (isGet(request)) return "join";
        String username = request.getParameter("username");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        if (!password1.equals(password2)) {
            request.setAttribute("flash", "Passwords don't match");
            return "join";
        }
        try {
            if (dao.userExists(username)) {
                request.setAttribute("flash", "That username is taken");
                return "join";
            }
        }
        catch (SQLException sqle) {
            request.setAttribute("flash", sqle.getMessage());
            return "join";
        }
        try {
            User user = new User(username, password2);            
            Profile profile = new Profile();
            dao.addProfile(profile);
            user.setProfile(profile);
            dao.addUser(user);       
            request.getSession().setAttribute("user", user);
            return "redirect:timeline";
        }
        catch (IllegalArgumentException | SQLException e) {
            request.setAttribute("flash", e.getMessage());
        }
        return "join";
    }
    
    private String post(HttpServletRequest request) {
        if (notLoggedIn(request)) return "redirect:timeline";
        if (isGet(request)) return "post";
        try {
            String content = request.getParameter("content");
            Post post = new Post(content, this.getSessionUser(request));
            dao.addPost(post);
            request.setAttribute("lastPost", post);
            return "post";
        }
        catch (IllegalArgumentException iae) {
            request.setAttribute("flash", iae.getMessage());
        }
        catch (SQLException sqle) {
            request.setAttribute("flash", sqle.getMessage());
        }
        return "post";
    }
    
    private String profile(HttpServletRequest request) {
        if (notLoggedIn(request)) return "redirect:timeline";
        if (isGet(request)) {
            String target = request.getParameter("for");
            if (target == null || target.length() == 0)
                request.setAttribute("target", getSessionUser(request));
            else {
                try {
                    User targetUser = dao.findUserByUsername(target);
                    request.setAttribute("target", targetUser);
                }
                catch (SQLException sqle) {
                    request.setAttribute("flash", sqle.getMessage());
                }
            }
            return "profile";
        }
        
        // POST MAPPING
        try {
            Profile current = this.getSessionUser(request).getProfile();
            Profile temp = new Profile();
            temp.setFirstName(request.getParameter("firstName"));
            temp.setLastName(request.getParameter("lastName"));
            temp.setEmailAddress(request.getParameter("emailAddress"));
            temp.setBiography(request.getParameter("biography"));
            dao.updateProfile(current, temp);
            request.setAttribute("success", "Profile updated");
            request.setAttribute("target", this.getSessionUser(request));
        }
        catch (SQLException | IllegalArgumentException e) {
            request.setAttribute("flash", e.getMessage());
        }
        return "profile";
    }
    
    private String avatar(HttpServletRequest request) {
        if (this.isGet(request)) return "upload";

        try {
            final Part filePart = request.getPart("avatar");
            String filename = filePart.getSubmittedFileName();
            String filetype = filePart.getContentType();
            if (!filetype.contains("image")) {
                request.setAttribute("flash", "The uploaded file is not an image");
                return "upload";
            }
            InputStream data = filePart.getInputStream();
            User user = this.getSessionUser(request);
            dao.updateAvatar(user, filetype, data);
        } catch (IOException | SQLException | ServletException e) {
            request.setAttribute("flash", e.getMessage());
        }
        return "upload";
    }
    
    private String revert(HttpServletRequest request) {
        try {
            dao.revertAvatarFor(this.getSessionUser(request));
        } catch (SQLException sqle) {
            request.setAttribute("flash", sqle.getMessage());
        }
        request.setAttribute("target", this.getSessionUser(request));
        return "profile";
    }
    
    private String follow(HttpServletRequest request) {
        if (notLoggedIn(request)) return "redirect:timeline";
        User user = this.getSessionUser(request);
        String targetName = request.getParameter("target");
        if (targetName.equalsIgnoreCase(user.getUsername()))
            return "redirect:wall";
        try {
            User target = dao.findUserByUsername(targetName);
            dao.follow(user, target);
            return "redirect:wall&for=" + targetName;
        }
        catch (SQLException sqle) {
            request.setAttribute("flash", sqle.getMessage());
            return "wall&for=" + targetName;
        }
    }
    
    private String unfollow(HttpServletRequest request) {
        if (notLoggedIn(request)) return "redirect:timeline";
        User user = this.getSessionUser(request);
        String targetName = request.getParameter("target");
        if (targetName.equalsIgnoreCase(user.getUsername()))
            return "profile&for=" + targetName;
        try {
            User target = dao.findUserByUsername(targetName);
            dao.unfollow(user, target);
            return "redirect:wall&for=" + targetName;
        }
        catch (SQLException sqle) {
            request.setAttribute("flash", sqle.getMessage());
        }
        return "redirect:wall&for=" + targetName;
    }

    private HubbubDao getDao() {
        @SuppressWarnings("unchecked")
        HubbubDao hdao = (HubbubDao)this.getServletContext().getAttribute("dao");
        return hdao;
    }
    
    public User getSessionUser(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        User user = (User)request.getSession().getAttribute("user");
        return user;
    }
    
    private boolean loggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("user") != null;
    }
   
    private boolean notLoggedIn(HttpServletRequest request) {
        return !loggedIn(request);
    }
    
    private boolean isGet(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("GET");
    }
    
    private int getTimelinePageSize() {
        return Integer.parseInt(this.getServletContext().getInitParameter("page.size"));
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
