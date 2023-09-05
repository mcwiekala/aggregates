# Optimistic Locking 

This project highlights the challenges associated with overly large clusters of entities in applications with high concurrency.

In this example, multiple unrelated scenarios involve various actors. However, ORM tempts developers to merge multiple tables through relationships such as:
 - one to one 
 - many to one / one to many
 - many to many

This is very easy to achieve. However, it results in increasingly larger transactions. Consequently, the application's performance is degraded and leads to numerous blocking issues that are very difficult to fix.

![big-cluster-of-entities.png](docs%2Fbig-cluster-of-entities.png)


Problems are related to `ObjectOptimisticLockingFailureException` caused by `StaleObjectStateException`

 - **Synchronous operations** - Might happen in synchronous scenarios when there is stale object with old entity version number.
[Check this test.](src/test/java/io/cwiekala/agregates/UserSyncIT.java)
 - **Asynchronous operations** - scenario as above or concurrent changes with multiple users competing with each other. Changes are on the same table
[Check this test.](src/test/java/io/cwiekala/agregates/UserAsyncIT.java)

## Database

This project uses docker and Postgres DB, because embedded H2 does not support parallel transactions

http://www.h2database.com/html/advanced.html#mvcc

## Lost Update anomaly

![lost-updates.png](docs%2Flost-updates.png)

_image credits to [Vlad Mihalcea](https://vladmihalcea.com/a-beginners-guide-to-database-locking-and-the-lost-update-phenomena/)_


