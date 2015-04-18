package controller;

import entities.Message;
import controller.util.JsfUtil;
import controller.util.PaginationHelper;
import entities.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;



@ManagedBean(name = "messageController")
@SessionScoped
public class MessageController implements Serializable {

    @PersistenceContext(unitName = "AllUNeedIsMoneyPU")
    private EntityManager em;
    private Message current;
    private DataModel items = null;
    private int tmp=0;
    private int toAnswer=0;
    private String oldContent="";
    @EJB
    private controller.MessageFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public MessageController() {
    }
    
    public Message getSelected() {
        if (current == null) {
            current = new Message();
            selectedItemIndex = -1;
        }
        return current;
    }

    private MessageFacade getFacade() {
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
    
    public String prepareReply(int destID,String _oldContent){
        toAnswer = destID;
        oldContent = _oldContent;
        recreateModel();
        return "Reply";
    }
    
    public int getToAnswer(){
        return toAnswer;
    }
    public DataModel getUserItem(String userName)
    {
        User usr = (User) em.createNamedQuery("User.findByNickname").setParameter("nickname", userName).getSingleResult();
        List<Message> msg = em.createNamedQuery("Message.findByUserid").setParameter("userid", usr.getId()).getResultList();
        List<Message> printMsg=new ArrayList<Message>();
        for(Message mess : msg)
        {
            Message toAdd = mess;
            if(mess.getContent().length()>49)
            {
                
                toAdd.setContent(mess.getContent().substring(0, 50)+". . .");
                
            }
            printMsg.add(toAdd);
            
        }
        items= new ListDataModel(printMsg);
        return items;
    }

    public String prepareView() {
        Message msg =  (Message)getItems().getRowData();
        current = (Message)em.createNamedQuery("Message.findById").setParameter("id", msg.getId()).getSingleResult();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Message();
        selectedItemIndex = -1;
        return "newMessage";
    }

    public String create(String destUser) {
        try {
            System.out.println("destUser: "+destUser);
            if(destUser==null || destUser=="")
            {
                System.out.println("toAnswer: "+toAnswer);
                current.setUserid(toAnswer);
                current.setContent(current.getContent() + "SAUT_LIGNE SAUT_LIGNE ----- old ----- SAUT_LIGNE SAUT_LIGNE" + oldContent);
            }
            else
            {
                FacesContext fc = FacesContext.getCurrentInstance();
                Map<String,String> params = fc.getExternalContext().getRequestParameterMap();

                List<User> dest = em.createNamedQuery("User.findByNickname").setParameter("nickname", destUser).getResultList();
                current.setUserid(dest.get(0).getId());
            }

            current.setSenderid(tmp);
            System.out.println(current.getSenderid());
            System.out.println(current.getContent());
            System.out.println(current.getUserid());
            current.setContent(current.getContent().replaceAll("(\\r|\\n)", "SAUT_LIGNE"));
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MessageCreated"));
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Message) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MessageUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Message) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("MessageDeleted"));
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
    
    public void setUser(String userName)
        {
            List<User> sender = em.createNamedQuery("User.findByNickname").setParameter("nickname", userName).getResultList();
            tmp=sender.get(0).getId();
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

    @FacesConverter(forClass = Message.class)
    public static class MessageControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MessageController controller = (MessageController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "messageController");
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
            if (object instanceof Message) {
                Message o = (Message) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Message.class.getName());
            }
        }

    }

}
