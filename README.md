# Optimistic Locking 

This project highlights the challenges associated with overly large clusters of entities in applications with high concurrency.

In this example, multiple unrelated scenarios involve various actors. However, ORM tempts developers to merge multiple tables through relationships such as:
 - one to one 
 - many to one / one to many
 - many to many

This process is straightforward to execute. However, it results in increasingly larger transactions. Consequently, the application's performance deteriorates, leading to numerous blocking issues that are very difficult to fix.

![big-cluster-of-entities.png](docs%2Fbig-cluster-of-entities.png)


`ObjectOptimisticLockingFailureException` caused by `StaleObjectStateException`

 - **Synchronous operations** - Might happen in synchronous scenarios when there is stale object with old entity version number.
see:
 - **Asynchronous operations** - concurrent changes with multiple users competing with each other. Changes are in the same table 
see:

## Database

This project uses docker and Postgres DB, because embedded H2 does not support parallel transactions

http://www.h2database.com/html/advanced.html#mvcc

## Lost Update anomaly

![lost-updates.png](docs%2Flost-updates.png)

_image credits to [Vlad Mihalcea](https://vladmihalcea.com/a-beginners-guide-to-database-locking-and-the-lost-update-phenomena/)_


