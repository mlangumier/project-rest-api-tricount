package fr.hb.mlang.projectrestapi.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The {@link User} of the application. Can create or participate in {@link Group}s, pay
 * {@link Expense}s or accumulate expenses debts by getting from other users {@link ExpenseShare}s,
 * and pay {@link Settlement} to other users or receive some from them.
 */
@Entity
@Table(name = "user_table")
public class User implements UserDetails, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "name")
  private String name;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "password")
  private String password;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Group> ownedGroups = new HashSet<>();

  @ManyToMany(mappedBy = "members")
  private Set<Group> groups = new HashSet<>();

  @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL)
  private Set<Expense> expenses = new HashSet<>();

  @OneToMany(mappedBy = "debtor", cascade = CascadeType.ALL)
  private Set<ExpenseShare> expensesShares = new HashSet<>();

  @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL)
  private Set<Settlement> settlementsPaid = new HashSet<>();

  @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL)
  private Set<Settlement> settlementsReceived = new HashSet<>();

  /**
   * Default constructor
   */
  public User() {
    // Required by JPA
  }

  /**
   * Entity constructor
   *
   * @param id       UUID identifier
   * @param name     Name of the user
   * @param email    Email credential
   * @param password Password credential
   */
  public User(UUID id, String name, String email, String password) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
  }

  /**
   * Factory method that creates a temporary user without credentials
   *
   * @param name Name of the user
   * @return a minimal visitor
   */
  public static User temporaryUser(String name) {
    User user = new User();
    user.setName(name);
    return user;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Group> getOwnedGroups() {
    return ownedGroups;
  }

  public void setOwnedGroups(Set<Group> ownedGroups) {
    this.ownedGroups = ownedGroups;
  }

  public Set<Group> getGroups() {
    return groups;
  }

  public void setGroups(Set<Group> groups) {
    this.groups = groups;
  }

  public Set<Expense> getExpenses() {
    return expenses;
  }

  public void setExpenses(Set<Expense> expenses) {
    this.expenses = expenses;
  }

  public Set<ExpenseShare> getExpensesShares() {
    return expensesShares;
  }

  public void setExpensesShares(
      Set<ExpenseShare> expensesShares
  ) {
    this.expensesShares = expensesShares;
  }

  public Set<Settlement> getSettlementsPaid() {
    return settlementsPaid;
  }

  public void setSettlementsPaid(
      Set<Settlement> settlementsPaid
  ) {
    this.settlementsPaid = settlementsPaid;
  }

  public Set<Settlement> getSettlementsReceived() {
    return settlementsReceived;
  }

  public void setSettlementsReceived(
      Set<Settlement> settlementsReceived
  ) {
    this.settlementsReceived = settlementsReceived;
  }

  //--- Helper methods

  public void addOwnedGroup(Group group) {
    group.setOwner(this);
    this.ownedGroups.add(group);
  }

  public void removeOwnedGroup(Group group) {
    this.ownedGroups.remove(group);
    group.setOwner(null);
  }

  public void addGroup(Group group) {
    group.addMember(this);
    this.groups.add(group);
  }

  public void removeGroup(Group group) {
    this.groups.remove(group);
    group.removeMember(this);
  }

  public void addExpense(Expense expense) {
    expense.setPaidBy(this);
    this.expenses.add(expense);
  }

  public void removeExpense(Expense expense) {
    this.expenses.remove(expense);
    expense.setPaidBy(null);
  }

  public void addExpenseShare(ExpenseShare expenseShare) {
    expenseShare.setDebtor(this);
    this.expensesShares.add(expenseShare);
  }

  public void removeExpenseShare(ExpenseShare expenseShare) {
    this.expensesShares.remove(expenseShare);
    expenseShare.setDebtor(null);
  }

  public void addSettlementPaid(Settlement settlement) {
    settlement.setFromUser(this);
    this.settlementsPaid.add(settlement);
  }

  public void removeSettlementPaid(Settlement settlement) {
    this.settlementsPaid.remove(settlement);
    settlement.setFromUser(null);
  }

  public void addSettlementReceived(Settlement settlement) {
    settlement.setToUser(this);
    this.settlementsReceived.add(settlement);
  }

  public void removeSettlementReceived(Settlement settlement) {
    this.settlementsReceived.remove(settlement);
    settlement.setToUser(null);
  }

  //--- UserDetails methods

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getUsername() {
    return this.getEmail();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof User user)) {
      return false;
    }
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public String toString() {
    return "User{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", password='" + password + '\'' +
        '}';
  }
}
