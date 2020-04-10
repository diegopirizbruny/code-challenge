
Diego Piriz's answer to LEOVEGAS JAVA ENGINEER CODING CHALLENGE
---------------------------------------------------------------

This is a Play Framework 2.8 project to implement all the requirements listed in the challenge description.
[Challenge Description](https://c.smartrecruiters.com/sr-company-attachments-prod/53a13fa7e4b0a3111eed2e30/525e9d87-3a5d-415b-a63f-1f689974c691?r=s3)

# Running

Locally

    ./sbt run
    ./sbt test

Run defaults to localhost:9000, please check Play's documentation if you want to change any default value

Docker

    ./sbt docker:publish
    ./sbt docker:publishLocal
    docker container run --publish 9000:9000 --detach diegopiriz/code-challenge:1.0.0 -Dhttp.address=0.0.0.0

You can also find the docker at dockerhub (exposed port is 9000):
     docker pull diegopiriz/code-challenge:1.0.0

# Implementation

I chose Play Framework because it's very straight forward to build REST APIs with it.
The main reason to choose it for this project:
 * Runs on the JVM
 * Stateless by nature (good for scaling)
 * EBean is easy to use and auto-generates db schema (see conf/evolutions/default/1.sql)
 * I have experience with it

Design
------
The code is organized in the standard structure of a Play project keeping in mind to separate responsibilities.

 Models: They are POJOs without logic (or very little). EBean enhances those classes adding repository capacities.
 Logic: The business logic is here.
 Controllers:
    Request: Contains the helper classes that parse and validate request bodies (for POST requests)
    Responses: A little overkill, but encapsulates the response formats
    Wallet: The controller that glues all together and calls the appropriate logic method.

The instructions were unclear in many aspects so I made some assumptions:
1. "Monetary account" it's vague. What currency/currencies? Precision? Rounding? etc.
   I decided to model all monetary amounts as BigDecimal to avoid rounding errors but it's assumed all amounts are
   in the same currency.
2. Sign of the amount? (ie: debits' posted amount is negative or positive?)
   I didn't impose any restrictions.
   Positive Credits and negative Debits add to balance.
   Negative Credits and positive Debits subtract form the balance.
3. "Current balance per player".
   What happens if the player doesn't exist?
   I decided to throw an error in this case and expose another endpoint /createWallet to create a player without
   transactions and balance = 0.
4. "transaction id that must be unique for all transactions".
   What format are ids?
   I assumed nothing about them. I modeled playerId and Transaction Id as unrestricted Strings.
5. Request/Response formats?
   I assumed Json request and responses.
   Status 200 for successful operations and 400 for error ones.
6. "The solution need not persist data across restarts but it is a bonus if it does"
   I was using SQLite as it's pretty standard and doesn't require you to install anything. But the next point imposes
   to use an in-memory database so I switched back to H2. Therefore, current solution doesn't persist state across
   runs.

Clean Code
----------
"The proper use of comments is to compensate for our failure to express ourselves in code." 
Clean Code - Robert C. Martin

Testability
-----------
Maybe the weak point of this project.
To do proper Unit testing of the logic class I would need to encapsulate the Models in a Repository 
pattern (see [here](https://www.playframework.com/documentation/2.8.x/JavaTest#Unit-testing-models) for details) but,
it seemed to much for a project that runs on an in-memory database (it' cheap to go directly to the db).

I believe the basic Unit Testing and the more extensive Functional Tests are enough.

Software craftsmanship
----------------------
Up to you to judge, but there is only one IF statement in the whole code :)

Concurrency  & Atomicity
------------------------
These were important factors to decide the general architecture and technology.
Play Framework handles concurrent requests transparently, that's one less problem for me to solve.
H2 (SQLite, or any other database) also takes care of concurrent writes and race conditions. I can just send
request to the database and be sure that it will handle them correctly.

There was only one tricky point (and probably the only real problem to solve in this project) and is the balance.
There is no "best" design on how to handle the balance (calculated on the fly, extra field in transaction, extra field 
in a parent table, checkpoints, etc) and the final decision will depend on the use cases and their frequency.
I will not go deep on the pros and cons of each method (I'm sure you know them, if not read 
[here](https://stackoverflow.com/questions/4373968/database-design-calculating-the-account-balance)).

The problem description didn't give me enough information to choose the best design, my decision was based on the fact
that the balance needs to be "checked" on every debit (and probably credit, if you allow negative credits) and
therefore calculating it on the fly (the DRY way) was not the best option.

I decided to store the current balance in the parent table (Player) as a separate field.
To keep that field consistent I did what you would expect:
    1- Start Transaction (DB transaction)
    2- Lock Player for Update (to prevent changes in the current balance)
    3- Refresh Balance (despite unlikely, the balance could have changed between 1 and 2)
    4- Insert Transaction (credit-debit)
    5- Update Balance
    6- Commit (or Rollback)

(basically, an atomic "read then update")

Scalability
-----------
The current solution doesn't scale (it's using a local in-memory db). But by changing appilcation.conf to use any
relational external database you could deploy this to many servers (ie: behind a load balancer).
Play servers are stateless (ie: interchangeable)

Idempotency
-----------
Again, I'm letting the DB take care of that. Idempotency of Credit/Debit operations is given by the database as the
transactions table uses the given transaction Id as the primary key. Any repeated attempt to insert will fail.
