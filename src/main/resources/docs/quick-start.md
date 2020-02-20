- [What you'll build](#build)
- [What you'll learn](#learn)
- [What you'll need](#need)
- [Step 1: Setup your environment](#setup)
- [Step 2: Create and add the starter project to your local machine](#create-project)
    - [Create to-do project on GitHub and clone to your local machine](#github)
    - [Open to-do project on your local machine and make some top level configs](#configs)
- [Step 3: Setup your database](#setup-database)
    - [Create the to-do database in Sequel Pro](#sequel-pro)
    - [Connect your to-do project to MySQL database](#connect-db)
- [Step 4: Setup your task entity](#setup-entity)
- [Step 5: Setup your task controller](#setup-controller)
- [Step 6: Migrate tables to database](#migrate-table)
- [Step 7: Update the front end](#update-ui)
- [Step 8: Run the to-do app and play around](#run-app)

    

<a name="build"></a>
### [What you'll build](#build)
In this guide, you will quickly get Alpas up-and-running on your local machine and create a simple to-do app.

Don't worry about your skill level - this quick start guide is meant to provide step-by-step instructions useful to those of any skill level. 💪🏽  
### What you'll learn
>/tip/ 
> <span>You'll learn quite a bit building this simple app.
>
><div class="sublist">
>
>- How to get Alpas setup and running on your local device
>- How to connect with a MySQL database
>- How to create database tables in Alpas and migrate them
>- How to create, retrieve, update, and delete data in your MySQL database
>- How to create a database entity object
>- How to create a controller
>- How to add routes
>- How to interact with the MySQL database using Ozone
>- How to protect your app against the cross site request forgery attacks
>- How to connect an interactive front-end with the powerful Alpas back-end
>- How to create a fun, yet useful to-do list! ✅
>
></div>


And, this only scratches the surface of Alpas's features! 

<a name="need"></a>
### [What you'll need](#need)

<div class="">

Before moving to the first step, go through the following list and make sure you have everything you will need to get started. 
Don't worry, everything you need is free to use! 

- ✓ An Integrated Developer Environment (IDE) - we will be using the [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) Community version
- ✓ A GitHub account - If you don't have a GitHub account, [create an account here](https://github.com/join?source=header-home)
- ✓ MySQL Server installed and running on your local machine.
    - &nbsp; &nbsp; &nbsp; &nbsp; - [Instructions for installing on Mac](https://tableplus.com/blog/2018/11/how-to-download-mysql-mac.html)
    - &nbsp; &nbsp; &nbsp; &nbsp; - [Instructions for installing on Windows](https://dev.mysql.com/doc/refman/8.0/en/windows-installation.html)
- ✓ [Sequel Pro](https://sequelpro.com/download) to manage and view our to-do database.
- ⏳ Just a little bit of time - this will only take about 15 - 20 minutes of your time 🙌

</div>

<div class="border-t mt-8"></div>

### Let's Get Started!

>/info/
><span>If you have previously successfully used the starter template and served an Alpas app, jump ahead to 
>[Step 3: Setup your database][step3]</span>

<a name="setup"></a>
### [Step 1: Setup your environment](#setup)

Setting up your environment is quick and painless. 

1. Refer to the [setup instructions](/docs/installation#setup) and perform the necessary steps .
2. If using Windows, be sure to watch [**Setting up GitBash and SDKMan on Windows**](https://kutt.it/sDS63q) and
[**Alpas Installation on Windows With WSL**](https://kutt.it/18hxT8) by [*AlpasCasts*](https://kutt.it/XnILn0).
3. Make sure that you have installed Java 9.0.7-zulu and gradle.

<a name="create-project"></a>
### [Step 2: Create and add the starter project to your local machine](#create-project)

Now that your environment is all setup, let's get rollin' on creating your very first Alpas app! 🎉

The following steps are taken from the [Installation](/docs/installation#installation) section, but we will fill in some additional details for your to-do app. 

<a name="github"></a>
#### [Create to-do project on GitHub and clone to your local machine](#github)


1. Go to the [Alpas Starter project](https://github.com/alpas/starter) on GitHub
2. Click the green **Use this template** button
3. On the 'Create a new repository from starter' page, type **todolist** for 'Repository name'
4. Click **Create repository from template** - you now copied the startup template to your GitHub account!
5. On your new todolist repo page, select **Clone or download** 
6. You will have an option to use HTTPS or SSH - let's use **HTTPS** for now
7. Pull the repo down to your local 


<a name="configs"></a>
#### [Open to-do project on your local machine and make some top level configs](#configs)


1. Open IntelliJ and go to **File > Open** and find the root folder, **todolist**, on your machine
2. Double click **todolist** to open 
3. If you are prompted to **import Gradle**, go ahead and do so
4. In IntelliJ, locate the **Terminal** tab 
5. In the terminal, type in `chmod +x ./alpas`<span class="clipboard" data-clipboard-text='chmod +x ./alpas'></span> and hit return - this will make Alpas executable
6. Next, in terminal, type `./alpas init com.todo.list`<span class="clipboard" data-clipboard-text='./alpas init com.todo.list'></span> and hit return - this will name your app
7. While we are in terminal, type `./alpas help`<span class="clipboard" data-clipboard-text='./alpas help'></span> - this displays a list of commands you can run to find information about your app as well as provide quick actions such as creating new entities 
8. Before we run the project, let's check to make sure IntelliJ is set to build using Java-9.0.7-zulu - Go to **File > Project Structure, then go to Project and make sure Project SDK has **Java 9** selected**; if it doesn't, follow instructions on how to serve Alpas with IntelliJ in the [Installation documentation](/docs/installation)
9. Now, find **start.kt** in your project - you can either tap the shift key twice to open quick search or follow the path **todolist > src > kotlin > start.kt**
10. Right click on **start.kt** and select option to **Run**
11. Once run is successful, click [http://localhost:8080/](http://localhost:8080/) to view the project in your browser


**TADA!** 🎉 - you are viewing the basic Alpas start screen. But, there is still a bit more to do to create your to-do list. Now that we have Alpas successfully installed and running
on your machine, let's create your to-do list! 

<a name="setup-database"></a>
### [Step 3: Setup your database](#setup-database)
Let's first start by creating a new database on your local machine. If you haven't already installed MySQL Server and have it running on your machine, refer to the [What you'll need](#need) section for links to set up MySQL. 

<a name="sequel-pro"></a>
#### [Create the to-do database in Sequel Pro](#sequel-pro)


1. Open **Sequel Pro**
2. Add a new connection 
3. Type **127.0.0.1** into Host 
4. Type in **root** for the Username
5. If you entered a password when installing MySQL, add in the password; otherwise, leave it blank
6. Click **Connect**
7. Once connected to your local MySQL server, go to **Database > Add Database**
8. Create a new database named **todolist**


That's all for now in Sequel Pro. We will add tables and fields during the next steps. 

<a name="connect-db"></a>
#### [Connect your to-do project to MySQL database](#connect-db)


1. Go back to the to-do project up in IntelliJ
2. Navigate to the **.env** file under the project root folder
3. In .env, update `DB_PASSWORD=secret` to replace 'secret' with your password; leave blank if you did not setup your MySQL database with a password
4. Update `DB_DATABASE` to point to `todolist`
5. Go to the **DatabaseConfig** file in **todolist > src > main > kotlin > configs** 
6. Uncomment `// addConnections(env)` by removing `//`
7. **Run** the project 


If you run and are able to successfully access [http://localhost:8080/](http://localhost:8080/), then congrats! You have successfully connected the project to your MySQL database. 

<a name="setup-entity"></a>
### [Step 4: Setup your task entity](#setup-entity)


1. In terminal, type `./alpas make:entity task`<span class="clipboard" data-clipboard-text='./alpas make:entity task'></span> and tap return - this will create a new entity 'Task' and corresponding table
 under **src > main > kotlin > entities** folder
2. Update **task.kt** to look like the following - check out the helper comments in the code 


<span class="line-numbers" data-start="1" data-file="src/main/kotlin/entities/Task.kt">


```kotlin

package com.todo.list.entities

import dev.alpas.ozone.*
import me.liuwj.ktorm.schema.boolean
import java.time.Instant

interface Task : OzoneEntity<Task> {
    var id: Long
    var name: String?
    var createdAt: Instant?
    var updatedAt: Instant?
    
    // add a boolean property for completed todos
    val completed: Boolean

    companion object : OzoneEntity.Of<Task>()
}

object Tasks : OzoneTable<Task>("tasks") {
    val id by bigIncrements()
    val name by string("name").size(150).nullable().bindTo { it.name }
    val createdAt by createdAt()
    val updatedAt by updatedAt()

    // add a completed column for completed todos
    val completed by boolean("completed").default(false).bindTo { it.completed }
}

// for more information on Entities, visit https://alpas.dev/docs/entity-relationship#main

```

</span>

<a name="setup-controller"></a>
### [Step 5: Setup your task controller](#setup-controller)


1. In IntelliJ's terminal, type `./alpas make:controller TaskController`<span class="clipboard" data-clipboard-text='./alpas make:controller TaskController'></span> and tap return - this creates a 'TaskController'
under **todolist > src > main > kotlin > controllers** folder
2. Update **TaskController.kt** to look like the following - check out the helper comments in the code 


<span class="line-numbers" data-start="1" data-file="src/main/kotlin/controllers/TaskController.kt">

```kotlin

package com.todo.list.controllers

import dev.alpas.http.HttpCall
import dev.alpas.routing.Controller

// add the following imports
import com.todo.list.entities.Tasks // This calls the Tasks entity created in step 4
import dev.alpas.orAbort
import me.liuwj.ktorm.dsl.delete
import me.liuwj.ktorm.dsl.update
import dev.alpas.ozone.create
import dev.alpas.ozone.latest
import dev.alpas.validation.min
import dev.alpas.validation.required
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.toList

class TaskController : Controller() {
    fun index(call: HttpCall) {

        // Go ahead and remove the following line
        // call.reply("Hello, TaskController!")

        // add in the following
        
        // calls the database for all todo items
        val tasks = Tasks.latest().toList()  
        // referring to the previous call, this checks to see how many todos are in the database
        val total = tasks.size 
        // now, we see how many of the total todos have been completed
        val completed = tasks.count { it.completed } 
        
        // Now we will call the front end page template to display to the user, passing along the values we defined above
        call.render ("welcome", mapOf("tasks" to tasks, "total" to total, "completed" to completed))
    }
    
    // Let's create a function to store new todo items that have been added via the front end
    fun store(call: HttpCall) {
        
        // Before we write the new todo task to the database, let's first validate to make sure there is data with at least 2 characters
        // If validation fails, a message will be sent back to front end with the failure reasons
        call.applyRules("newTask") {
            required()
            min(2)
        }.validate() 
        
        // If validation has passed, the next part will write new data to database
        Tasks.create() {
            it.name to call.stringParam("newTask") // this pulls the todo text from the http call
        }
        
        // If new todo task has successfully been written to db, a success message is sent back to front end
        flash("success", "Successfully added to-do")
        call.redirect().back()
    }
    
    // Next up, let's create a function to delete a todo task
    fun delete(call: HttpCall) {
        // We will get the todo tasks id that is marked for deletion and then remove todo from database
        val id = call.longParam("id").orAbort() 
        Tasks.delete { it.id eq id }
        flash("success", "Successfully removed to-do")
        call.redirect().back()
    }
    
    // Lastly, let's create a function to update a todo task as being completed; or, to reverse completion state
    fun update(call: HttpCall) {
        val id = call.longParam("id").orAbort()
        
        // Let's get the current boolean state of todo task and then change state
        val markAsComplete = call.param("state") != null 
        
        Tasks.update { 
            it.completed to markAsComplete
            where {
                it.id eq id
            }
        }
        
        // Based on if markAsComplete is equal to 'True', let's flash the appropriate message
        if (markAsComplete) {
            flash("success", "Successfully completed the to-do")
        } else {
            flash("success", "Successfully updated the to-do")
        }
        
        call.redirect().back()
    }
}

// For more information on Controllers, visit https://alpas.dev/docs/controllers#main

```

</span>

<a name="setup-routes"></a>
### [Step 6: Setup your routes](#setup-routes)


1. Navigate to the new **routes.kt** at **todolist > src > main > kotlin**
2. Update the page to look like the following - check out the helper comments in the code 


<span class="line-numbers" data-start="1" data-file="src/main/kotlin/routes.kt">


```kotlin

package com.todo.list

// Update WelcomeController to TaskController
import com.todo.list.controllers.TaskController 
import dev.alpas.routing.RouteGroup
import dev.alpas.routing.Router

// https://alpas.dev/docs/routing
fun Router.addRoutes() = apply {
    group {
        webRoutesGroup()
    }.middlewareGroup("web")

    apiRoutes()
}

private fun RouteGroup.webRoutesGroup() {
    // Update WelcomeController to TaskController
    get("/", TaskController::index).name("welcome") 

    // Add the following routes
    post("/", TaskController::store).name("store")
    delete("/", TaskController::delete).name("delete")
    patch("/", TaskController::update).name("update")
}

private fun Router.apiRoutes() {
    // register API routes here
}

```

</span>

<a name="migrate-table"></a>
### [Step 6: Migrate tables to database](#migrate-table)

We are super close! Just a few more steps! In this step we will [create the to-do table and migrate](https://alpas.dev/docs/migrations#main) to the todolist database. 


1. In terminal, type `./alpas make:migration create_tasks_table --create=tasks`<span class="clipboard" data-clipboard-text='./alpas make:migration create_tasks_table --create=tasks'></span> - this will create new data migration script
2. In terminal, type `./alpas db:migrate`<span class="clipboard" data-clipboard-text='./alpas db:migrate'></span> this will migrate the tasks table and columns we created earlier to your checklist db - go ahead and check it out in Sequel Pro


<a name="update-ui"></a>
### [Step 7: Update the front end](#update-ui)

This last major step is all about updating the front end. 

In IntelliJ, open the **welcome.peb** file at **todolist > src > main > resources > templates** and update to look like the following. 

<span class="line-numbers" data-start="1" data-file="src/main/resources/templates/welcome.peb">


```twig

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <title>Alpas - To-Do List</title>
  <link rel="stylesheet" href="{{ mix('css/app.css') }}">
  <link href="https://fonts.googleapis.com/css?family=Open+Sans&display=swap" rel="stylesheet">
</head>

<body class="bg-orange-100 h-screen">

<!-- This section displays the flash messages received from TaskController -->
<div class="container-fluid fixed w-full flex justify-center">
  {% if hasFlash('success') %}
    <div class="w-1/3 text-white bg-green-500 py-2 px-10 text-center">
      {{ flash('success') }}
    </div>
  {% endif %}
</div>

<div class="container mx-auto pt-20 px-20">
  <div class="w-full flex justify-between mx-auto">
    <div class="w-1/2 text-4xl font-extrabold text-gray-700"> To-Do List</div>
    <div class="text-center">
      <!-- Here we show completed tasks vs total tasks -->
      <h5 class="text-4xl font-extrabold text-blue-600"> {{ completed }} / {{ total }}</h5>
      <p class="text-sm text-gray-600">completed / total</p>
    </div>
  </div>

  <div>
    <!-- This form is where the user enters the to-do item and makes a post request -->
    <form method="POST" action="{{ route('store') }}">
      <div class="form-group">
        <!-- CSRF is a protection mechanism; view https://alpas.dev/docs/csrf-protection#main for more info -->
        {% csrf %}
        <input type="text" class="form-control mt-2 w-1/3 py-3 px-2 bg-gray-200 rounded-sm" id="task" name="newTask"
               autofocus="autofocus"
               placeholder="Go fishin'">
        <button type="submit" class="ml-2 p-3 px-8 inline-block bg-green-500 text-green-100">Add</button>
      </div>
      <!-- This checks to make sure to-do entry is a valid entry -->
      {% if hasError("newTask") %}
        <div class="text-red-600 mt-3">{{ firstError("newTask") }}</div>
      {% endif %}
    </form>
  </div>

  <div class="py-10">
    <!-- If no to-dos, display the following message -->
    {% if total == 0 %}
      <div class="alert alert-warning">
        Add some to-dos!
      </div>
    {% else %}
      <!-- If to-dos do exist, then we iterate on displaying each to-do item -->
      <div>
        <ul class="text-xl">
          {% for task in tasks %}
            <li>
              <div class="flex pt-1">
                <div class="pr-4">
                  <!-- This form let's the user mark a todo task as completed or not and makes patch call to update database -->
                  <form action="{{ route('update', {"id": task.id}) }}" method="POST">
                    {% csrf %}
                    <input type="hidden" name="_method" value="patch"/>
                    <input type="checkbox" name="state"
                           onChange="this.form.submit()" {{ task.completed ? 'checked' : '' }} />
                    {# {% if task.completed %} #}
                    {# {% csrf %} #}
                    {# <input type="hidden" name="_method" value="patch"/> #}
                    {# <input type="checkbox" onChange="this.form.submit()" checked /> #}
                    {# {% else %} #}
                    {# {% csrf %} #}
                    {# <input type="hidden" name="_method" value="patch"/> #}
                    {# <input type="checkbox" onChange="this.form.submit()" /> #}
                    {# {% endif %} #}
                  </form>
                </div>

                {% if task.completed %}
                  <div class="line-through">
                    {{ task.name }}
                  </div>
                {% else %}
                  <div class="">
                    {{ task.name }}
                  </div>
                {% endif %}
                <div class="pl-3">
                  <!-- This form lets user delete a to-do task -->
                  <form action="{{ route('delete', {"id": task.id}) }}" method="POST">
                    {% csrf %}
                    <input type="hidden" name="_method" value="delete"/>
                    <button class="hover:text-red-800 text-red-600 text-sm hover-target">
                      remove
                    </button>
                  </form>
                </div>
              </div>
            </li>
          {% endfor %}
        </ul>
      </div>
    {% endif %}
  </div>
</div>

</body>

</html>

```

</span>

<a name="run-app"></a>
### [Step 8: Run the to-do app and play around](#run-app)

1. In IntelliJ, click **Run** to run app
2. Once successfully running, click on the localhost to view the to-do list

**HURRAY!!! You created your very first Alpas app!!!** 🎉🎉🎉

Now, play around with your new to-do list. 🕹


[step3]: #setup-database

