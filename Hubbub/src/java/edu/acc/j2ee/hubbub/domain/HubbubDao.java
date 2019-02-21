package edu.acc.j2ee.hubbub.domain;

import edu.acc.j2ee.hubbub.HashTool;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HubbubDao {
    private final Connection conn;
    
    public HubbubDao(Connection conn) {
        this.conn = conn;
    }

    public void addUser(User u) throws SQLException {
        u.setHash(HashTool.hash(u.getPassword()));        
        String sql = "INSERT INTO users (username,password,profile) VALUES (?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, u.getUsername());
            pst.setString(2, u.getHash());
            pst.setInt(3, u.getProfile().getId());
            pst.executeUpdate();            
        }
    }
    
    public User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT password,profile FROM users WHERE username = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    int profileId = rs.getInt("profile");
                    Profile profile = this.findProfileByIdAndUsername(profileId, username);
                    User user = new User();
                    user.setUsername(username);
                    user.setHash(storedHash);
                    user.setProfile(profile);
                    return user;
                }
                else return null;
            }
        }
    }
    
    public User findUserByUsernameAndPassword(String username, String password) throws SQLException {
        User user = this.findUserByUsername(username);
        return (user == null || !HashTool.compare(password, user.getHash())) ?
                null : user;
    }
    
    public boolean userExists(String username) throws SQLException {
        User user = this.findUserByUsername(username);
        return user != null;
    }

    public void addPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (author,content,posted) VALUES (?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, post.getAuthor().getUsername());
            pst.setString(2, post.getContent());
            pst.setTimestamp(3, new Timestamp(post.getPostDate().getTime()));
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                post.setId(rs.getInt(1));
            }
        }
    }

    public List<Post> findPostsInRange(int page, int size) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY posted DESC OFFSET ?" +
                " ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setInt(1, page * size);
            stat.setInt(2, size);
            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    User author = this.findUserByUsername(rs.getString("author"));
                    String content = rs.getString("content");
                    java.util.Date postDate = rs.getTimestamp("posted");
                    int id = rs.getInt("id");
                    Post post = new Post(content, author, postDate, id);
                    posts.add(post);
                }
                return posts;
             }
        }
     }

    public int countAllPosts() throws SQLException {
        String sql = "SELECT COUNT(id) AS n FROM posts";
        try (Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(sql)) {
            rs.next();
            return rs.getInt("n");
        }
    }
    
    public List<Post> findUserPostsInRange(User author, int page, int size) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE author = ? ORDER BY posted " +
                "DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, author.getUsername());
            stat.setInt(2, page * size);
            stat.setInt(3, size);
            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    String content = rs.getString("content");
                    Date postDate = rs.getDate("posted");
                    int id = rs.getInt("id");
                    Post post = new Post(content, author, postDate, id);
                    posts.add(post);
                }
                return posts;
            }
        }
    }

    public int countUserPosts(User author) throws SQLException {
        String sql = "SELECT COUNT(id) AS n FROM posts WHERE author = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, author.getUsername());
            try (ResultSet rs = stat.executeQuery()) {
                rs.next();
                return rs.getInt("n");
            }
        }
    }
    
    public Profile findProfileByIdAndUsername(int id, String username) throws SQLException {
        String sql = "SELECT * FROM profiles WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");
                    Date joinDate = rs.getDate("joined");
                    String email = rs.getString("email");
                    String biography = rs.getString("biography");
                    String mime = rs.getString("mime");
                    byte[] avatar = rs.getBytes("avatar");
                    List<String> followees = findFollowingByUsername(username, "follower", "followee");
                    List<String> followers = findFollowingByUsername(username, "followee", "follower");
                    Profile profile = new Profile(firstName, lastName, joinDate, email, biography, id);
                    profile.setMime(mime);
                    profile.setAvatar(avatar);
                    profile.setFollowees(followees);
                    profile.setFollowers(followers);
                    return profile;
                }
                else
                    return null; // should never happen
            }
        }
    }
    
    public List<String> findFollowingByUsername(String username, String major, String minor)
    throws SQLException {
        String sql = String.format(
                "SELECT %s FROM following WHERE %s = ? ORDER BY %s",
                minor, major, minor);
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                List<String> result = new ArrayList<>();
                while (rs.next()) {
                    String name = rs.getString(minor);
                    result.add(name);
                }
                return result;
            }
        }
    }

    public void addProfile(Profile profile) throws SQLException {
        String sql = "INSERT INTO profiles (joined) VALUES (?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setDate(1, new Date(profile.getJoinDate().getTime()));
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                profile.setId(rs.getInt(1));
            }
        }       
    }

    public void updateProfile(Profile owned, Profile bean) throws SQLException {
        String sql = "UPDATE profiles SET firstname = ?, lastname = ?, email " +
                    "= ?,  biography = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, bean.getFirstName());
            pst.setString(2, bean.getLastName());
            pst.setString(3, bean.getEmailAddress());
            pst.setString(4, bean.getBiography());
            pst.setInt(5, owned.getId());
            pst.executeUpdate();
            owned.setFirstName(bean.getFirstName());
            owned.setLastName(bean.getLastName());
            owned.setEmailAddress(bean.getEmailAddress());
            owned.setBiography(bean.getBiography());
        }
    }
    
    public void updateAvatar(User user, String mime, InputStream is) throws SQLException {
        byte[] imgdata = null;
        try {
            imgdata = imageFromStream(is);
        }
        catch (IOException ioe) {
            throw new SQLException(ioe);
        }
        String sql = "UPDATE profiles SET avatar = ?, mime = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setBytes(1, imgdata);
            pst.setString(2, mime);
            pst.setInt(3, user.getProfile().getId());
            pst.executeUpdate();
            user.getProfile().setMime(mime);
            user.getProfile().setAvatar(imgdata);
        }      
    }
    
    private byte[] imageFromStream(InputStream is) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[0xFFFF];
            for (int len; (len = is.read(buffer)) != -1;)
                os.write(buffer, 0, len);
            os.flush();
            return os.toByteArray();
        }          
    }
    
    public void revertAvatarFor(User user) throws SQLException {
        String sql = "UPDATE profiles SET avatar = NULL, mime = NULL WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, user.getProfile().getId());
            pst.executeUpdate();
            user.getProfile().setAvatar(null);
            user.getProfile().setMime(null);
        }
    }
    
    public void follow(User user, User target) throws SQLException {
        String sql = "INSERT INTO following VALUES (?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, user.getUsername());
            pst.setString(2, target.getUsername());
            pst.executeUpdate();
            user.getProfile().getFollowees().add(target.getUsername());
            target.getProfile().getFollowers().add(user.getUsername());
        }
    }
    
    public void unfollow(User user, User target) throws SQLException {
        String sql = "DELETE FROM following WHERE follower = ? AND followee = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, user.getUsername());
            pst.setString(2, target.getUsername());
            pst.executeUpdate();
            user.getProfile().getFollowees().remove(target.getUsername());
            target.getProfile().getFollowers().remove(user.getUsername());
        }
    }
}
