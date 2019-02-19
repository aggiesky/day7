package edu.acc.j2ee.hubbub;

import edu.acc.j2ee.hubbub.domain.user.User;
import edu.acc.j2ee.hubbub.domain.post.Post;
import edu.acc.j2ee.hubbub.domain.profile.Profile;
import java.io.IOException;
import java.io.InputStream;
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
            // try to create a user - invalid params throw IllegalArgumentException
            User candidate = new User(request.getParameter("username"),
                    request.getParameter("password"));
            User user = dao.findUserByUsernameAndPassword(candidate.getUsername(),
                    candidate.getPassword());
            if (user == null) {
                request.setAttribute("flash", "Access Denied");
                return "login";
            }
            request.getSession().setAttribute("user", user);
        }
        catch (Exception e) {
            request.setAttribute("flash", e.getMessage());
        }
        return "redirect:timeline";
    }
    
    private String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:timeline";
    }
    
    private String timeline(HttpServletRequest request) {
        Pager pager = Pager.of(request.getParameter("page"), getTimelinePageSize(),
                dao.countAllPosts());
        List<Post> posts = dao.findPostsInRange(pager.getPage(), pager.getPageSize());
        request.setAttribute("pager", pager);
        request.setAttribute("posts", posts);
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
        catch (DaoException de) {
            request.setAttribute("flash", de.getMessage());
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
        catch (Exception e) {
            request.setAttribute("flash", e.getMessage());
            return "join";
        }
        try {
            User user = new User(username, password2);        
            Profile profile = new Profile(user);
            dao.addUser(user);
            dao.addProfile(profile);        
            request.getSession().setAttribute("user", user);
            return "redirect:timeline";
        }
        catch (Exception e){
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
            request.setAttribute("flash",
                "Yer post content must be between 1 and 255 characters!");
        }
        catch (DaoException de) {
            request.setAttribute("flash", de.getMessage());
        }
        return "post";
    }
    
    private String profile(HttpServletRequest request) {
        if (notLoggedIn(request)) return "redirect:timeline";
        if (isGet(request)) {
            try {
                User target = request.getParameter("for") == null ?
                    this.getSessionUser(request) :
                    dao.findUserByUsername(request.getParameter("for"));
                Profile profile = dao.findProfileByUser(target);
                request.setAttribute("profile", profile);
                request.setAttribute("target", target);
                return "profile";
            }
            catch (Exception e) {
                request.setAttribute("flash", "Couldn't retrieve profile: " +
                        e.getMessage());
            }
        }
        
        // POST mapping
        Profile current = null;
        try {
            current = dao.findProfileByUser(this.getSessionUser(request));
            Profile temp = new Profile();
            temp.setFirstName(request.getParameter("firstName"));
            temp.setLastName(request.getParameter("lastName"));
            temp.setEmailAddress(request.getParameter("emailAddress"));
            temp.setBiography(request.getParameter("biography"));
            dao.updateProfile(current, temp);
            request.setAttribute("success", "Profile updated");
        }
        catch (IllegalArgumentException | DaoException e) {
            request.setAttribute("flash", e.getMessage());
        }
        request.setAttribute("profile", current);
        return "profile";
    }
    
    private String avatar(HttpServletRequest request) {
        if (this.isGet(request)) 
            return "upload";
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
            Profile profile = dao.findProfileByUser(user);
            request.setAttribute("profile", profile);
        } catch (IOException | ServletException e) {
            request.setAttribute("flash", e.getMessage());
        }
        return "upload";
    }

    private HubbubDao getDao() {
        @SuppressWarnings("unchecked")
        HubbubDao dao = (HubbubDao)this.getServletContext().getAttribute("dao");
        return dao;
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
