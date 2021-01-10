package onlineShop.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import onlineShop.entity.Authorities;
import onlineShop.entity.Customer;
import onlineShop.entity.User;

@Repository
// @Repository is related to database, it's ok for using @Component
public class CustomerDao {

  @Autowired
  // connect to database, see line 17 in ApplicationConfig
  private SessionFactory sessionFactory;

  public void addCustomer(Customer customer) {
    // assign an authority, a normal user's authority by default
    Authorities authorities = new Authorities();
    authorities.setAuthorities("ROLE_USER");
    authorities.setEmailId(customer.getUser().getEmailId());
    Session session = null;

    try {
      session = sessionFactory.openSession();
      // re-save data once happened error to avoid dirty data (data inconsistent)
      session.beginTransaction();
      session.save(authorities);
      session.save(customer);
      session.getTransaction().commit();
    } catch (Exception e) {
      e.printStackTrace();
      session.getTransaction().rollback();  // roll back if it happens data insertion error
    } finally {
      if (session != null) {
        session.close();
      }
    }
  }

  // I use email as userName in the app, so the parameter name could be change to email
  public Customer getCustomerByUserName(String userName) {
    User user = null;
    try (Session session = sessionFactory.openSession()) {
      // searching condition
      Criteria criteria = session.createCriteria(User.class);
      user = (User) criteria.add(Restrictions.eq("emailId", userName)).uniqueResult();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (user != null)
      return user.getCustomer();
    return null;
  }
}

