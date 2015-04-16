package controller;

import entities.Contact;
import controller.util.JsfUtil;
import controller.util.PaginationHelper;
import entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ManagedBean(name = "contactController")
@SessionScoped
public class ContactController implements Serializable {

    @PersistenceContext(unitName = "AllUNeedIsMoneyPU")
    private EntityManager em;
    private Contact current;
    private DataModel items = null;
    @EJB
    private controller.ContactFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ContactController() {
    }

    public Contact getSelected() {
        if (current == null) {
            current = new Contact();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ContactFacade getFacade() {
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
        current = (Contact) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }
    
    public String prepareCreate() {
        current = new Contact();
        selectedItemIndex = -1;
        return "newContact";
    }

    /*public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ContactCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }*/
    
    public String create(String currentUser, String contactUser) {
        try {
            current = new Contact();
            User user = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", currentUser).getSingleResult();
            User contact = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", contactUser).getSingleResult();
            
            current.setUser(user);
            current.setUserid(user.getId());
            current.setContactid(contact.getId());
            
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ContactCreated"));
            return "List";
            //return null;
        } catch (Exception e) {
            System.out.println("*****************ERROR**********************");
            System.out.println(e.getMessage());
            System.out.println(e.getClass());
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            
            return null;
        }       
    }
    
    public List<User> getContact(String user)
    {
        User currentUser = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", user).getSingleResult();
        List<User> listUserContact = new ArrayList<User>();
        User contactUser = new User();
        List<Contact> listContact = em.createNamedQuery("Contact.findByUserid").setParameter("userid", currentUser.getId()).getResultList();
        List<Contact> listUser = em.createNamedQuery("Contact.findByContactid").setParameter("contactid", currentUser.getId()).getResultList();
        for (Contact contact : listUser) {
            contactUser = (User) em.createNamedQuery("User.findById").setParameter("id", contact.getUserid()).getSingleResult();
            listUserContact.add(contactUser);
        }
        for (Contact contact : listContact) {
            contactUser = (User) em.createNamedQuery("User.findById").setParameter("id", contact.getContactid()).getSingleResult();
            listUserContact.add(contactUser);
        }
        return listUserContact;
    }

    public boolean checkIfExist(String currentUser, String contactUser)
    {
       User user = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", currentUser).getSingleResult();
       User contact = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", contactUser).getSingleResult();
       List<Contact> contactExist = em.createNamedQuery("Contact.findByUseridAndContactid").setParameter("userid", user.getId()).setParameter("contactid", contact.getId()).getResultList();
       return !contactExist.isEmpty();
    }
    public String prepareEdit() {
        current = (Contact) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ContactUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Contact) getItems().getRowData();
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

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("ContactDeleted"));
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

    @FacesConverter(forClass = Contact.class)
    public static class ContactControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ContactController controller = (ContactController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "contactController");
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
            if (object instanceof Contact) {
                Contact o = (Contact) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Contact.class.getName());
            }
        }

    }

}
