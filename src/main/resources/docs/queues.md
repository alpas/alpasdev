- [Registering Queue Connections](#registering-queue-connections)
- [Jobs](#jobs)
    - [Creating](#creating-jobs)
    - [Queuing](#queuing-jobs)
    - [Delaying](#delaying-job-processing)
    - [Retrying](#retrying-failed-jobs)
- [Available Drivers](#available-drivers)    
    - [Pass-through](#passthrough)
    - [Database](#database)
    - [ActiveMQ](#activemq)
- [Running Queue](#running-queue)
    - [Setting Connection](#setting-connection)
    - [Selecting Queues](#selecting-queues)
    - [Waiting for Jobs](#waiting-for-jobs)

Alpas makes it easy to defer time-consuming tasks to a queue for processing it later. You can choose
one of the many queues bundled with Alpas or create one of your own. No matter what you choose,
Alpas abstracts them away behind a cohesive set of APIs. You can easily switch between
different queue backends without having to change your code.

The backends for queues are called `connections` in Alpas such as `DatabaseQueueConnection`. Each connection
could have multiple queues that you could selectively use for queuing different tasks most possibly
based on their priorities such as *high priority queue*, *low priority queue* etc.

<a name="registering-queue-connections"></a>
### [Registering Queue Connections](#registering-queue-connections)

If you open `configs/Queue.kt`, you'll notice that Alpas has lazily registered three queue connections for you.
Each connection is regestered with a name. Alpas uses this key to pick one of these connections based on
the value of `QUEUE_CONNECTION` in your `.env` file. This is set to `passthrough` by default.

Feel free to register more queue connections or remove the ones that you know for sure you would't use.

Once you are happy with your queue connections, make sure to register `QueueServiceProvider::class` 
by [adding it to the list of service providers](/docs/service-providers#registering) in both
kernel classes—`HttpKernel` and `ConsoleKernel`.

<a name="jobs"></a>
### [Jobs](#jobs)

<a name="creating-jobs"></a>
#### [Creating Jobs](#creating-jobs)

A job is just a serializable task with some metadata that you want to process later. The
metadata is used during the processing of the task once it is de-queued from the backend.

Here's a real example of a job that is used when [sending a mail](/docs/mail) such as when
[resetting passwords](/docs/password-reset).

<span class="line-numbers" data-start="2">

```kotlin

class SendMailJob(val mail: MailMessage) : Job() {
    override fun invoke(container: Container) {
        this(container.config<MailConfig>().driver())
    }

    operator fun invoke(mailDriver: MailDriver) {
        mailDriver.send(mail)
    }
}

```

</span>

You can create a job by extending `dev.alpas.queue.job.Job` class and overriding `invoke()` method, which will
be called after it is retrieved from the queue for actual processing of the job. The easiest way to create
a job is by using `make:job` Alpas console command. The jobs will be placed under `jobs` folder.

```bash

$ alpas make:job SendInvoice

```

<span class="line-numbers" data-start="2" data-file="jobs/SendMailJob.kt">

```kotlin

class SendInvoice : Job() {
    override fun invoke(container: Container) {
        // make and send the actual invoice
    }
}

```

</span>

<a name="queuing-jobs"></a>
#### [Queuing Jobs](#queuing-jobs)

You can queue a job by asking a container for a `QueueDispatcher` instance and then calling its `dispatch()` method.

<span class="line-numbers" data-start="10">

```kotlin

// ...

// Enqueue MailInvoice job to be processed by the default
// queue connection from the queue name called "high"
container.make<QueueDispatcher>.dispatch(MailInvoice(), "high")

// ...

```

</span>

Conveniently, since most of the times you would be queueing a job from within a controller,
you can call base controller's `queue()` method.

<span class="line-numbers" data-start="6">

```kotlin

fun sendInvoice(call: HttpCall){
    queue(MailInvoice(), "medium")
}

```

</span>

If you want to queue a job on a non-default connection, you can pass the name of the connection as a third parameter:

<span class="line-numbers" data-start="10">

```kotlin

// ...

container.make<QueueDispatcher>.dispatch(MailInvoice(), "high", "database")

// ...

```

</span>

<span class="line-numbers" data-start="5">

```kotlin

fun sendInvoice(call: HttpCall){
    queue(MailInvoice(), "medium", "database")
}

```

</span>

<a name="delaying-job-processing"></a>
#### [Delaying Job Processing](#delaying-job-processing)

If you would like to delay the processing of a queued job after you have dispatched it, you can do so
by overriding `delayInSeconds` property of the job. It is set to **1 second** by default, which
means it will be available to be processed within approx. 1 second of adding it to the queue.

<a name="retrying-failed-jobs"></a>
#### [Retrying Failed Jobs](#retrying-failed-jobs)

Although undesirable, your job might fail during processing for one reason or the other. Usually when that
happens you would want to give few chances for that job to be processed again. You can do so by
overriding `tries` property of the job. It is set to **3 tries** by default. If the job keeps
failing even after the number of tries set in the job, it will be added to a failed job
queue for you to do further introspection.

> /alert/ <span>A job instance gets serialized as a JSON before putting it in a queue. This means a job must
>be serializable along with all its properties and dependencies. Also, make sure that the values injected
>via primary constructor are not private otherwise the job will fail to deserialize, and it won't be
>processed.</span>

<a name="available-drivers"></a>
### [Available Drivers](#available-drivers)

Alpas comes bundled with 3 queue drivers:

- [Pass-through (*passthrough*)](#passthrough)
- [Database (*database*)](#database)
- [ActiveMQ (*activemq*)](#activemq)

<a name="passthrough"></a>
#### [Pass-through](#passthrough)

A `passthrough` driver simply invokes the job without holding it in queue. This is useful if you want to quickly debug
or test your jobs without having to have them go through a queue, as it needs some ceremonies to run a queue worker.

> /alert/ <span> Keep in mind that since the jobs are invoked right away with the `passthrough` driver, it
completely ignores both `tries` and `delayInSeconds` properties.

<a name="database"></a>
#### [Database](#database)

You can put your jobs in a database by setting `QUEUE_CONNECTION` value in your `.env` file to `database`. In order
to hold the jobs in the database, you'd need two tables—one for holding the actual jobs and another for holding
the failed jobs. You can use `queue:tables` Alpas command to generate a pre-built migration for you. Just
like every other migrations, the migration will be put under `database/migrations` folder. Once the
migration is created, you can migrate your database using `migrate` Alpas command.

```bash

# first publish the migration
$ alpas queue:tables

# re-build the app with new migrations
$ alpas build

# then migrate the database
$ alpas migrate

```

<a name="activemq"></a>
#### [Active MQ](#activemq)

For a more robust, flexible, and cross-platform queuing, you can use [ActiveMQ][activemq], which Alpas
supports out-of-the-box. It requires a small ceremony to set up a messaging server, called a broker,
but once that is done, you get the one of the most popular messaging servers for queueing your jobs.

Checkout the [official ActiveMQ website][activemq] for more information of plethora
of other features it provides out-of-the-box.

Since Alpas abstracts away the intricacies of interacting with different queue drivers, nothing
changes as far as queueing, de-queueing, and processing of jobs is concerned. You do need
to set few values in you `.env` file:

<span data-file=".env">

```toml

QUEUE_CONNECTION=activemq

# Change the following values based on your ActiveMQ setup
ACTIVEMQ_HOST=localhost
ACTIVEMQ_PORT=5672
ACTIVEMQ_USERNAME=admin
ACTIVEMQ_PASSWORD=password
ACTIVEMQ_NAMESPACE=myapp

# The following values are optional. They will be set by Alpas automatically if they
# are not set in your .env file. We do recommend setting it manually though. Just
# make sure they are unique for each apps that use the same broker.
ACTIVEMQ_DEFAULT_QUEUE_NAME=default
ACTIVEMQ_FAILED_QUEUE_NAME=failed

```

</span>

Now all that remains is to set up an ActiveMQ broker either on your local machine or on a remote server.

Let's do the minimum setup of the next version of ActiveMQ, [ActiveMQ Artemis][artemis], and play with it.

<div class="ordered-list">

1. Download the latest version of [ActiveMQ Artemis][artemis-download], move it to a more permanent
location, unzip it, and rename the unarchived folder to something like **artemis**.
2. Open a terminal and `cd` into the `artemis` folder.
3. We first need to create a new broker:

```bash

# This creates a new broker named *mybroker* with a username called *admin*,
# requires that a login is required for # configuring the broker, and lastly,
# saves all the data to a folder named *mybroker* in the current directory.
# When you hit enter, this will first ask you to provide the default password.

$ bin/artemis create --name=mybroker --user=admin --require-login ./mybroker

```

4. Once created, you can run start `mybroker` broker using `aretemis run` command. If you always want
to run this broker, for an example in a production server, you can run it in the background by
using `artemis-service start` command. Let's run the broker before moving to the next step.

```bash

$ mybroker/bin/artemis run

# You should now have access to a full management console dashboard at your 
# disposal at, by default, http://localhost:8161/. We were not kidding when
# we said ActiveMQ is an enterprise grade messaging queue!

```

5. We would like to create queues on-demand rather than pre-configuring them before we use them. To be able
to do that, you need to open `mybroker/etc/broker.xml` file and set `auto-delete-queues` to `false`
and `default-address-routing-type` to `ANYCAST` under `<address-setting match="#">` element:

<span class="line-numbers" data-start="18" data-file="mybroker/etc/broker.xml">

```xml

<!-- ... -->

<address-setting match="#">
    <!-- ... -->
    <!-- ... -->
    <auto-delete-queues>false</auto-delete-queues>
    <default-address-routing-type>ANYCAST</default-address-routing-type>
</address-setting>

<!-- ... -->

```

</span>

</div>

Now your ActiveMQ broker is ready to accept your jobs and queue them. If you go to the ActiveMQ
dashboard, you'll be able to see all of your queued jobs and introspect them.

>/alert/ <span> To be able to queue jobs using ActiveMQ driver, the broker must be running. If not, the
>app will throw an exception and you will lose the job that was supposed to be put in a queue. Make
>sure that the broker is running before serving your app.

<a name="running-queue"></a>
### [Running Queue](#running-queue)

One of the ways you can process a job is by using `queue:work` Alpas command. When you run it,
Alpas basically starts a new console app instance, de-queues and deserializes a job from
a connection one-by-one, and processes them.

```bash

$ alpas queue:work

```

> /alert/ <span>The queue work process must be running continuously in the background to be able to process
jobs as they come in. Since the app is compiled and loaded once when you run `queue:work` command, you
must rebuild your app and then restart the process whenever your code changes.</span>

<a name="setting-connection"></a>
#### [Setting Connection](#setting-connection)

Queue worker selects the default queue connection specified in your `.env` file when de-queueing jobs from
a driver. You can specify which queue connection to pick by passing the name of the connection as an
argument to `queue:work` command.

```bash

# this command will process jobs from the 'database' connection
$ alpas queue:work database

```

<a name="selecting-queues"></a>
#### [Selecting Queues](#selecting-queues)

Not just the connection, you can also pick what queues you want to process by specifying
`--queue` option to `queue:work` command.

```bash

# this command will process jobs from 'invoices' queue on 'database' connection
$ alpas queue:work database --queue=invoices

```

<a name="waiting-for-jobs"></a>
#### [Waiting for Jobs](#waiting-for-jobs)

When you have no jobs available on your queue, then you don't want to bombard the driver checking for
jobs availability. To be easy on your system, you might want the worker to sleep for some time
before probing for jobs again. You can do this by passing `--sleep` option and specifying
the number of seconds the worker will sleep if there are no new jobs available.

```bash

# The worker will sleep for 5 seconds if there are no new jobs available
$ alpas queue:work --sleep=5

```

If you don't pass a value for the `sleep` option, each driver uses its own timeout value to determine how
long to sleep. For ActiveMQ, it is 30 seconds and for a database queue it is 3 seconds. You can tweak
the value for each connection individually while registering them in your `QueueConfig` class.

>/info/<span>Keep in mind that different drivers "sleep" differently. Database driver, for an example, just
>makes the worker thread go to sleep by calling `Thread.sleep()` method. On the other hand, ActiveMQ
>driver passes the sleep time as a parameter to `receive()` method as it is waiting for the next
>job to arrive. While the `passthrough` driver ignores this and doesn't sleep at all!</span>

- [activemq]: https://activemq.apache.org/
- [artemis]: https://activemq.apache.org/components/artemis/
- [artemis-download]: https://activemq.apache.org/components/artemis/download/
