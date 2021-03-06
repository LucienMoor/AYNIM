package controller;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import entities.User;
import controller.util.JsfUtil;
import controller.util.PaginationHelper;
import entities.Group1;
import entities.Picture;
import entities.UserGroup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.Part;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;


@ManagedBean(name = "userController")
@SessionScoped
public class UserController implements Serializable  {

    
    @PersistenceContext(unitName = "AllUNeedIsMoneyPU")
    private EntityManager em;
    private Part file;
    private User current;
    private DataModel items = null;
    @EJB
    private controller.UserFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    @Resource
    UserTransaction ut;

    public UserController() {
    }

    public String findNickname(int index)
    {
        User usr = (User) em.createNamedQuery("User.findById").setParameter("id", index).getSingleResult();
        return usr.getNickname();
    }
    public User getSelected() {
        if (current == null) {
            current = new User();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {        
        current = (User) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }
    
    public String prepareRoleEdition() {        
        current = (User) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "/adminSecure/richification.xhtml";
    }

    public String prepareMyView(String user)
    {
        current = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", user).getSingleResult();
        return "/user/View";
    }

    public String prepareCreate() {
        current = new User();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {

        if (file != null) {
            try {
                String filePath = uploadFile();

                current.setProfilPicture(filePath);
            } catch (IOException ex) {
                Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        else
        {
            current.setProfilPicture("smiley.png");
        }
        

        try {

            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            md.update(current.getPassword().getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] hash = md.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String hashedPassword = hexString.toString();
            current.setPassword(hashedPassword);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        try {
            current.setScore1("0");
            getFacade().create(current);
            Group1 group = (Group1) em.createNamedQuery("Group1.findByGroupName").setParameter("groupname", "PoorRole").getSingleResult();
            UserGroup userGroup = new UserGroup();
            userGroup.setNickname(current);
            userGroup.setGroupname(group);
            ut.begin();
            em.persist(userGroup);
            ut.commit();

            System.out.println("User created");
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserCreated"));
            return prepareCreate();
        } catch (Exception e) {
            System.out.println("*****************ERROR**********************");
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }
    
    public boolean isCurrentUser(String user, String currentUser)
    {
        User leUser = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", user).getSingleResult();
        User leCurrentUser = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", currentUser).getSingleResult();
        
        return leUser.getId()==leCurrentUser.getId();
        
    }
    
    public String search(String userSearch)
    {
        
        System.out.println(userSearch);
        List<User> results = em.createNamedQuery("User.findBySearch").setParameter("nickname", "%"+userSearch+"%").getResultList();
        items= new ListDataModel(results);
        return "/user/List.xhtml";
    }

    private String getFileName(Part part) {
        final String partHeader = part.getHeader("content-disposition");
        System.out.println("***** partHeader: " + partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim()
                        .replace("\"", "");
            }
        }
        return null;
    }
    

    private String uploadFile() throws IOException {

        // Extract file name from content-disposition header of file part
        String fileName = getFileName(getFile());
        String basePath = this.getClass().getResource("/images").getPath()+"/../../../resources/images/";
        System.out.println(basePath);
        File outputFilePath = new File(basePath + fileName);

        // Copy uploaded file to destination path
        InputStream inputStream = null;
        OutputStream outputStream = null;

        inputStream = getFile().getInputStream();
        outputStream = new FileOutputStream(outputFilePath);

        int read = 0;
        final byte[] bytes = new byte[1024];
        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }

        return fileName;
    }

    public String prepareEdit() {
        current = (User) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "/poorSecure/user/Edit.xhtml";
    }

    public String update() {

        if (file != null) {
            try {
                String filePath = uploadFile();

                current.setProfilPicture(filePath);
            } catch (IOException ex) {
                Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

        try {

            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            md.update(current.getPassword().getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] hash = md.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String hashedPassword = hexString.toString();
            current.setPassword(hashedPassword);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserUpdated"));
            return "/user/View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (User) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }
    
    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    public String destroyFromProfil() {
        performDestroyAndLogout();
        recreateModel();
        updateCurrentItem();
        return "/homePage.xhtml";
    }

    private void performDestroyAndLogout() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
            AuthenticationController.logout();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }
    
    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
            AuthenticationController.logout();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    /**
     * @return the file
     */
    public Part getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(Part file) {
        this.file = file;
    }

    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + User.class.getName());
            }
        }

    }

    public void validateFile(FacesContext ctx,
            UIComponent comp,
            Object value) {
        List<FacesMessage> msgs = new ArrayList<FacesMessage>();
        Part file = (Part) value;
        if (file.getSize() > 1024) {
            msgs.add(new FacesMessage("file too big"));
        }
        System.out.println(file.getContentType());
        if (!"image".equals(file.getContentType())) {
            msgs.add(new FacesMessage("not an image"));
        }
        if (!msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }
    
    public void makeRich()
    {
        Group1 group = (Group1) em.createNamedQuery("Group1.findByGroupName").setParameter("groupname", "RichRole").getSingleResult();
        UserGroup userGroup = (UserGroup) em.createNamedQuery("UserGroup.findByNickname").setParameter("nickname", current).getSingleResult();
        userGroup.setGroupname(group);
        try {
        ut.begin();
           em.merge(userGroup);
        ut.commit();
        }
        catch(Exception e)
        {
            
        }
    }
    
    public void makePoor()
    {
        Group1 group = (Group1) em.createNamedQuery("Group1.findByGroupName").setParameter("groupname", "PoorRole").getSingleResult();
        UserGroup userGroup = (UserGroup) em.createNamedQuery("UserGroup.findByNickname").setParameter("nickname", current).getSingleResult();
        userGroup.setGroupname(group);
        try {
            ut.begin();
               em.merge(userGroup);
            ut.commit();
        }
        catch(Exception e)
        {
            
        }
    }
    
    public boolean getIsRich()
    {
        UserGroup userGroup = (UserGroup) em.createNamedQuery("UserGroup.findByNickname").setParameter("nickname", current).getSingleResult();
        return userGroup.getGroupname().getGroupname().equals("RichRole");
    }
    
    public boolean getIsAdmin()
    {
        UserGroup userGroup = (UserGroup) em.createNamedQuery("UserGroup.findByNickname").setParameter("nickname", current).getSingleResult();
        return userGroup.getGroupname().getGroupname().equals("AdminRole");
    }
    
    public void addScore(User usr)
    {
        System.out.println("add ploint pls");
        String score = usr.getScore1();
        int iScore=Integer.parseInt(score);
        iScore+=1;
        usr.setScore1(String.valueOf(iScore));
        getFacade().edit(usr);
        
        
        
    }
    
    public String getPicture (String currentUser)
    {
        User user= (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", currentUser).getSingleResult();
        
        return user.getProfilPicture();
    }
}
