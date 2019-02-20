package edu.acc.j2ee.hubbub.domain;

import edu.acc.j2ee.hubbub.DaoException;
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
import java.util.ArrayList;
import java.util.List;

public class HubbubDao {
    private final Connection conn;
    
    public HubbubDao(Connection conn) {
        this.conn = conn;
    }

    public void addUser(User u) {
        u.setPassword(HashTool.hash(u.getPassword()));        
        String sql = "INSERT INTO users (username,password) VALUES (?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, u.getUsername());
            pst.setString(2, u.getPassword());
            pst.executeUpdate();            
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
        sql = "SELECT joined FROM users WHERE username = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, u.getUsername());
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                u.setJoinDate((rs.getDate("joined")));
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
    public User findUserByUsername(String username) {
        String sql = "SELECT password,joined FROM users WHERE username = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    Date joinDate = rs.getDate("joined");
                    return new User(username, storedHash, joinDate);
                }
                else return null;
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
    public User findUserByUsernameAndPassword(String username, String password) {
        User user = this.findUserByUsername(username);
        return (user == null || !HashTool.compare(password, user.getPassword())) ?
                null : user;
    }
    
    public boolean userExists(String username) {
        User user = this.findUserByUsername(username);
        return user != null;
    }

    public void addPost(Post post) {
        String sql = "INSERT INTO posts (author,content) VALUES (?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, post.getAuthor().getUsername());
            pst.setString(2, post.getContent());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                post.setId(rs.getInt(1));
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }       
        String sql2 = "SELECT posted FROM posts WHERE id = " + post.getId();
        try (
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql2)
        ) {
            rs.next();
            post.setPostDate(rs.getDate("posted"));
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
     }

    public List<Post> findPostsInRange(int page, int size) {
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
                    Date postDate = rs.getDate("posted");
                    int id = rs.getInt("id");
                    Post post = new Post(content, author, postDate, id);
                    posts.add(post);
                }
                return posts;
             }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
     }

    public int countAllPosts() {
        String sql = "SELECT COUNT(id) AS n FROM posts";
        try (Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(sql)) {
            rs.next();
            return rs.getInt("n");
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    public List<Post> findUserPostsInRange(User author, int page, int size) {
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
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    public int countUserPosts(User author) {
        String sql = "SELECT COUNT(id) AS n FROM posts WHERE author = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, author.getUsername());
            try (ResultSet rs = stat.executeQuery()) {
                rs.next();
                return rs.getInt("n");
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
    public Profile findProfileByUser(User owner) {
        String sql = "SELECT * FROM profiles WHERE owner = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, owner.getUsername());
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String email = rs.getString("email");
                String biography = rs.getString("biography");
                String mime = rs.getString("mime");
                byte[] avatar = rs.getBytes("avatar");
                List<User> followees = findFollowingByUser(owner, "follower", "followee");
                List<User> followers = findFollowingByUser(owner, "followee", "follower");
                int id = rs.getInt("id");
                Profile profile = new Profile(owner, firstName, lastName, email, biography, id);
                profile.setMime(mime);
                //if (avatar != null && avatar.length > 0)
                    profile.setAvatar(avatar);
                profile.setFollowees(followees);
                profile.setFollowers(followers);
                return profile;
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
    public List<User> findFollowingByUser(User user, String major, String minor) {
        String sql = String.format(
                "SELECT %s FROM following WHERE %s = ? ORDER BY %s",
                minor, major, minor);
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, user.getUsername());
            try (ResultSet rs = pst.executeQuery()) {
                List<User> result = new ArrayList<>();
                while (rs.next()) {
                    String name = rs.getString(minor);
                    User u = findUserByUsername(name);
                    result.add(u);
                }
                return result;
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    public void addProfile(Profile profile) {
        String sql = "INSERT INTO profiles (owner) VALUES (?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, profile.getOwner().getUsername());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                profile.setId(rs.getInt(1));
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }        
    }

    public void updateProfile(Profile owned, Profile bean) {
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
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
    public void updateAvatar(User user, String mime, InputStream is) {
        byte[] imgdata = imageFromStream(is);
        if (imgdata == null)
            return; // ok to have null avatar
        String sql = "UPDATE profiles SET avatar = ?, mime = ? WHERE owner = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setBytes(1, imgdata);
            pst.setString(2, mime);
            pst.setString(3, user.getUsername());
            pst.executeUpdate();
        } catch (SQLException sqle) {
            throw new DaoException(sqle);
        }        
    }
    
    private byte[] imageFromStream(InputStream is) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[0xFFFF];
            for (int len; (len = is.read(buffer)) != -1;)
                os.write(buffer, 0, len);
            os.flush();
            return os.toByteArray();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }          
    }
    
    public void revertAvatarFor(User user) {
        String sql = "UPDATE profiles SET avatar = NULL, mime = NULL WHERE owner = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, user.getUsername());
            pst.executeUpdate();
        } catch (SQLException sqle) {
            throw new DaoException(sqle);
        } 
    }
    
    public void follow(User user, User target) {
        String sql = "INSERT INTO following VALUES (?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, user.getUsername());
            pst.setString(2, target.getUsername());
            pst.executeUpdate();
            Profile userProfile = findProfileByUser(user);
            Profile targetProfile = findProfileByUser(target);
            userProfile.getFollowees().add(target);
            targetProfile.getFollowers().add(user);
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
}
