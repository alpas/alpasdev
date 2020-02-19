- [What you'll build](#build)
- [What you'll need](#need)
- [Step 1: Setup your environment](#setup)
- [Step 2: Create and add starter project to your local machine](#create-project)
    - [Create tasklist project on GitHub and clone to your local machine](#github)
    - [Open tasklist project on your local machine and make some top level configs](#configs)
- [Step 3: Setup your database](#setup-database)
    - [Create the tasklist database in Sequel Pro](#sequel-pro)
    

<a name="build"></a>
### [What you'll build](#build)
In this guide, you will quickly get Alpas up-and-running on your local machine and create a simple tasklist app.

Don't worry about your skill level - this quick start guide is meant to provide step-by-step instructions useful to those of any skill level. 💪🏽  

<a name="need"></a>
### [What you'll need](#need)
<div class="sublist">

- An Integrated Developer Environment (IDE). We will be using the [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=mac) Community version in this guide. And, don't worry, it's free to use! 
- A GitHub account - If you don't have a GitHub account, [create an account here.](https://github.com/join?source=header-home) 
- [Sourcetree](https://www.sourcetreeapp.com/) to manage your GIT Repo; we will also cover how to manage GIT directly through command line if you don't want to use Sourcetree.
- MySQL Server installed and running on your local machine 
    - [Instructions for installing SQL Server on Mac](https://tableplus.com/blog/2018/11/how-to-download-mysql-mac.html)
    - [Instructions for installing SQL Server on Windows](https://dev.mysql.com/doc/refman/8.0/en/windows-installation.html)
- [Sequel Pro](https://sequelpro.com/download).
- A little bit of time. ⏳ Seriously though - this will only take about 15 minutes of your time. 🙌

</div>

### Let's Get Started!


<a name="setup"></a>
### [Step 1: Setup your environment](#setup)

Setting up your environment is quick and painless. 

1. Refer to the [setup instructions](/docs/installation#setup) in the Installation doc and perform the necessary steps 
2. If using Windows, be sure to watch [**Setting up GitBash and SDKMan on Windows**](https://kutt.it/sDS63q) and
                                                                                                                                                [**Alpas Installation on Windows With WSL**](https://kutt.it/18hxT8) by
                                                                                                                                                [*AlpasCasts*](https://kutt.it/XnILn0)
    
3. Make sure that you have installed sdkman, Java 9.0.7-zulu, and gradle
4. If you haven't done so already, download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=mac)


<a name="create-project"></a>
### [Step 2: Create and add starter project to your local machine](#create-project)

Now that your environment is all setup, let's get rollin' on creating your very first Alpas app! 🎉

The following steps are taken from the [Installation](/docs/installation#installation) section, but we will fill in some additional details for your tasklist app. 

<a name="github"></a>
#### [Create tasklist project on GitHub and clone to your local machine](#github)

1. Go to the [Alpas Starter project](https://github.com/alpas/starter) on GitHub
2. Click the green **Use this template** button
3. On the 'Create a new repository from starter' page, type **tasklist** for 'Repository name'
4. Click **Create repository from template** - you now copied the startup template to your GitHub account!
5. On your new tasklist repo page, select **Clone or download** 
6. You will have an option to use HTTPS or SSH - let's use HTTPS for now
7. Select option to **Open in Desktop**
8. If you have Sourcetree installed, this will prompt a message to open via Sourcetree, which you can select that option to continue

> /info/ <span>**If you'd rather use command line to clone your repo, perform the 
> following steps instead of steps 5 thru 8 above.** 
> 1. Open Command Line in Windows; or, Terminal for Mac
> 2. Clone your repo by entering `git clone https://github.com/accountname/tasklist.git`<span class="clipboard" data-clipboard-text='git clone https://github.com/accountname/tasklist.git'></span> - swap **accountname** with your GitHub account's name
> 3. Enter `git checkout master`<span class="clipboard" data-clipboard-text='git checkout master'></span> to checkout the master branch
> 
> Having fun using command line? Here is a list of [helpful GIT commands](https://confluence.atlassian.com/bitbucketserver/basic-git-commands-776639767.html). 
> </span>

<a name="configs"></a>
#### [Open tasklist project on your local machine and make some top level configs](#configs)

1. Open IntelliJ and go to **File > Open** and find the root folder, **tasklist**, on your machine - *note: it's likely under your user directory unless you placed the project somewhere else*
2. Double click **tasklist** to open 
3. In IntelliJ, click on the **Terminal** tab (located towards the bottom left)
4. In the terminal, type in `chmod +x ./alpas`<span class="clipboard" data-clipboard-text='chmod +x ./alpas'></span> and hit return - this will make Alpas executable
5. Next, in terminal, type `./alpas init com.alpas.tasklist`<span class="clipboard" data-clipboard-text='./alpas init com.alpas.tasklist'></span> and hit return - this will name your app
6. While we are in terminal, type `./alpas help`<span class="clipboard" data-clipboard-text='./alpas help'></span> - this displays a list of commands you can run to find information about your app as well as provide quick actions such as creating new entities. 
7. Before we run the project, let's check to make sure IntelliJ is set to build using Java-9.0.7-zulu - Go to **File > Project Structure, then go to Project and make sure Project SDK has Java 9 selected**; if it doesn't, follow instructions on how to serve Alpas with IntelliJ in the [Installation section](/docs/installation)
8. Now, find **start.kt** in your project - you can either tap the shift key twice to open quick search or follow the path **tasklist > src > kotlin > start.kt**
9. Right click on **start.kt** and select option to **Run**
10. Once run is successful, click [http://localhost:8080/](http://localhost:8080/) to view the project in your browser

**TADA!** 🎉 - you are viewing the basic Alpas start screen. But, there is still a bit more to do to create your tasklist. Now that we have Alpas successfully installed and running
on your machine, let's create your tasklist! 

<a name="setup-database"></a>
### [Step 3: Setup your database](#setup-database)
Let's first start by creating a new database on your local machine. If you haven't already installed MySQL Server and have it running on your machine, refer to the [What you'll need](#need) section for links to set up MySQL. 

<a name="sequel-pro"></a>
#### [Create the tasklist database in Sequel Pro](#sequel-pro)

1. Open Sequel Pro
2. Add a new connection 
3. Type **127.0.0.1** into Host 
4. Type in **root** for the Username
5. If you entered a password when running MySQL, add in the password; otherwise, leave it blank
6. Click **Connect**
7. Once connected to your local MySQL server, go to **Database > Add Database**
8. Create a new database named **tasklist**

That's all for now in Sequel Pro. We will add tables and fields during the next steps. 

<a name="connect-db"></a>
#### [Connect your tasklist project to MySQL database](#connect-db)
1. Open the tasklist project up in IntelliJ
2. Navigate to the **.env** file under the project root folder
3. In .env, update `DB_PASSWORD=secret` to replace 'secret' with your password; leave blank if you did not setup your MySQL database with a password
4. Go to the **DatabaseConfig** file in **checklist > src > main > kotlin > configs** 
5. Uncomment `// addConnections(env)` by removing `//`
6. **Run** the project 

If you run and are able to successfully access [http://localhost:8080/](http://localhost:8080/), then congrats! You have successfully connected the project to your MySQL database. 

<a name="sequel-pro"></a>
### [Step 4: Setup your database](#setup-database)

1. In IntelliJ terminal, type `./alpas make:entity task`<span class="clipboard" data-clipboard-text='./alpas make:entity task'></span> and tap return - this will create a new entity 'task' and corresponding table
2. 

#### In Task.kt, 

replace interface Task : Entity<Task> {
            var id: Long
            var createdAt: Instant?
            var updatedAt: Instant?
        
            companion object : Entity.Factory<Task>()
        }
        
with 

interface Task : Entity<Task>, JsonSerializable {
    val id: Long
    val body: String
    val completed: Boolean
    val createdAt: Instant?
    val updatedAt: Instant?

    companion object : Entity.Factory<Task>()

    override fun toJson(): String {
        return JsonSerializer.serialize(this)
    }
}


Add (below id line)

    val body by text("body").bindTo { it.body }
    val completed by boolean("completed").default(false).bindTo { it.completed }

    
to 
    
object Tasks : MigratingTable<Task>("tasks") {
    val id by bigIncrements("id").bindTo { it.id }
    val createdAt by timestamp("created_at").nullable().bindTo { it.createdAt }
    val updatedAt by timestamp("updated_at").nullable().bindTo { it.updatedAt }
}}

Add to list of imports 
import dev.alpas.JsonSerializable
import dev.alpas.JsonSerializer
import me.liuwj.ktorm.schema.boolean
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.text


#### In routes.kt
 Add 
 
 private fun RouteGroup.webRoutesGroup() {
     get("/", WelcomeController::class).name("welcome")
     // register more web routes here
 
     group("projects") {
         addTaskRoutes()
     }.name("projects")
 }
 
 to (bellow //register more web routes here comment)
 
private fun RouteGroup.webRoutesGroup() {
    get("/", WelcomeController::class).name("welcome")
    // register more web routes here


Add following to bottom of page

private fun RouteGroup.addTaskRoutes() {
    group("<project>/tasks") {
        post(WelcomeController::class)
        delete("/<id>", WelcomeController::class)
        patch("/<id>", WelcomeController::class)
    }.name("tasks")
}


Create a new directory under kotlin called 'guards'
Create two kotlin files under guards - CreateTaskGuard.kt and UpdateTaskGaurd.kt

In CreateTaskGuard.kt, add following

package com.alpas.checklist.guards

import com.alpas.checklist.entities.Task
import com.alpas.checklist.entities.Tasks
import dev.alpas.ozone.create
import dev.alpas.validation.JsonField
import dev.alpas.validation.Rule
import dev.alpas.validation.ValidationGuard
import dev.alpas.validation.required

open class CreateTaskGuard : ValidationGuard() {
    override fun rules(): Map<String, Iterable<Rule>> {
        return mapOf("body" to listOf(JsonField(required())))
    }

    open fun commit(): Task {
        val now = call.nowInCurrentTimezone().toInstant()
        return Tasks.create {
            it.body to call.jsonBody?.get("body")
            it.createdAt to now
            it.updatedAt to now
        }
    }
}

In .kt, add following

package com.alpas.checklist.guards

import com.alpas.checklist.entities.Task
import com.alpas.checklist.entities.Tasks
import dev.alpas.orAbort
import dev.alpas.ozone.findOrFail
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.update

class UpdateTaskGuard : CreateTaskGuard() {
    override fun commit(): Task {
        val id = call.paramAsLong("id").orAbort()

        Tasks.update {
            it.body to call.jsonBody?.get("body")
            val isCompleted = call.jsonBody?.get("completed")
            if (isCompleted != null) {
                it.completed to isCompleted
            }
            where { it.id eq id }
        }
        return Tasks.findOrFail(id)
    }
}


Now, go to the database.migrations table and remove the create_users_table file
https://alpas.dev/docs/migrations#main

In terminal, type `./alpas make:migration create_tasks_table --create=tasks` - this will create new data migration script
In terminal, type `./alpas db:migrate` this will migrate the tasks table and columns we created earlier to your checklist db - go ahead and check it out in MySQL





in Welcome Controller, import the following
import com.alpas.checklist.guards.CreateTaskGuard
import com.alpas.checklist.guards.UpdateTaskGuard

> /alert/ <span>All the new apps are by default initialized to serve from port 8080. You will get an error
> if the port is already in use. If you want to use a different port, change the `APP_PORT` value in
> your `.env` file.</span>

> /power/ <span>Alpas runs on an embedded [Jetty Web Server](https://www.eclipse.org/jetty/).

[template]: https://github.com/alpas/starter
[intellij]: https://www.jetbrains.com/idea/download
