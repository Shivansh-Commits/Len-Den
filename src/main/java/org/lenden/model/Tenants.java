package org.lenden.model;

import java.time.LocalDateTime;

public class Tenants
{

    public String password;
    public String username;
    public LocalDateTime subscriptionStartDate;
    public LocalDateTime subscriptionEndDate;

    public LocalDateTime getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(LocalDateTime subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public LocalDateTime getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(LocalDateTime subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Tenants{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
