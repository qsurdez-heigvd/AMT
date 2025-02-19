# Report

TODO: Replace each section content with an appropriate answer according to instructions.
## Exercice 1 - Entity mappings

**Report** The tests provided that validate updating entities are using `EntityManager.flush()` and `EntityManager.clear()`) (e.g. `ActorRepositoryTest.testUpdateActor`).

* Describe precisely from the perspective of SQL statements sent to the database, the difference between:
    * `ActorRepositoryTest.testUpdateActor`
    * `ActorRepositoryTest.testUpdateActorWithoutFlushAndClear`
    * `ActorRepositoryTest.testUpdateActorWithoutClear`

* Explain the behavior differences and why it occurs.

Hints: run the tests using the debugger, look at the SQL statements in the log.

### Answer

We can see the number of queries differ from case to case. When `flush()` and `clear()` are called three statements are created. When only `flush()` is called, two statements are created. When none of them are called, only one statement is created. Further discussion on the behavior differences and why it occurs can be found in the following paragraphs.

First let's take a look at what `flush()` and `clear()` do.

##### `flush()`

The `flush()` method is used to synchronize the changes made to the entities with the database. This means that the changes made to the entities are persisted to the database. However, the entities are not detached from the `EntityManager`. This means that the `EntityManager` will still track the entities. In the configuration of the `EntityManager` we can either set it to automatically (`AUTO`) flush the changes to the database or manually (`COMMIT`) flush the changes to the database. When the `EntityManager` is set to automatically flush the changes to the database, the `flush()` method is called automatically when the transaction is committed. When the `EntityManager` is set to manually flush the changes to the database, the `flush()` method must be called manually.

##### `clear()`

The `clear()` method is used to detach all entities from the persistence context. This means that the `EntityManager` will no longer track the entities. If a new statement concerning an entity that was previously tracked by the persistence context is made, the `EntityManager` will retrieve fresh data from the database, thus creating a `SELECT` statement.

#### 1. `flush()` and `clear()`

When `flush()` is called, the `EntityManager` synchronizes the changes made to the entities with the database. The `clear()` method is used to detach all entities from the `EntityManager`. This means that the `EntityManager` will no longer track the entities. When `clear()` is called, the `EntityManager` will not be able to detect changes made to the entities. This is why we need to call `flush()` before `clear()`. The `flush()` method will synchronize the changes made to the entities with the database, and then `clear()` will detach the entities from the `EntityManager`. 

The third statement is a `select` query. This i smade by Hibernate to fetch the updated state of the entity from the database after the `flush()` and `clear()` method are called. 
Since `clear()` detaches all entities, Hibernate needs to re-fetch the entity to ensure it has the latest state when accessed again.

We can see here the three statements thus created : 

```sql
[Hibernate] 
    insert 
    into
        actor
        (first_name, last_name) 
    values
        (?, ?) 
    returning actor_id
2024-10-18 15:56:09,274 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (1:VARCHAR) <- [ALICE]
2024-10-18 15:56:09,275 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (2:VARCHAR) <- [BOB]
[Hibernate] 
    update
        actor 
    set
        first_name=?,
        last_name=? 
    where
        actor_id=?
2024-10-18 15:56:28,738 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (1:VARCHAR) <- [FOO]
2024-10-18 15:56:28,738 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (2:VARCHAR) <- [BAR]
2024-10-18 15:56:28,739 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (3:INTEGER) <- [201]
[Hibernate] 
    select
        a1_0.actor_id,
        a1_0.first_name,
        a1_0.last_name 
    from
        actor a1_0 
    where
        a1_0.actor_id=?
2024-10-18 15:56:37,284 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (1:INTEGER) <- [201]
```

#### 2. Only`flush()`

When only `flush()` is called, the `EntityManager` synchronizes the changes made to the entities with the database. However, the entities are not detached from the `EntityManager`. This means that the `EntityManager` will still track the entities. This is why we can see only two statements created. The first statement is the `insert` statement, and the second statement is the `update` statement. The third statement is thus not necessary. 

Here we do not see the `select` statement because the entities are still within the persistence context. 

```sql
[Hibernate] 
    insert 
    into
        actor
        (first_name, last_name) 
    values
        (?, ?) 
    returning actor_id
2024-10-18 16:12:21,786 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (1:VARCHAR) <- [ALICE]
2024-10-18 16:12:21,786 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (2:VARCHAR) <- [BOB]
[Hibernate] 
    update
        actor 
    set
        first_name=?,
        last_name=? 
    where
        actor_id=?
2024-10-18 16:12:24,928 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (1:VARCHAR) <- [FOO]
2024-10-18 16:12:24,928 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (2:VARCHAR) <- [BAR]
2024-10-18 16:12:24,929 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (3:INTEGER) <- [201]
```

#### 3. No `flush()` and `clear()`

When neither `flush()` nor `clear()` is called, the `EntityManager` will not synchronize the changes made to the entities with the database. This means that the changes will not be persisted to the database until the transaction is commited. 
This is why we can see only one statement created. 
The statement is the `insert` statement. The `update` statement is not created because the changes are not synchronized with the database.

```sql
[Hibernate] 
    insert 
    into
        actor
        (first_name, last_name) 
    values
        (?, ?) 
    returning actor_id
2024-10-18 16:15:27,715 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (1:VARCHAR) <- [ALICE]
2024-10-18 16:15:27,716 TRACE [org.hib.orm.jdb.bind] (main) binding parameter (2:VARCHAR) <- [BOB]
```

## Exercice 2 - Querying

**Report** on the query language that you prefer and why.
