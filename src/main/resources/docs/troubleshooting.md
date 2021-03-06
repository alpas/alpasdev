<div class="sublist">

- **After scaffolding the project, `alpas serve` throws an error.**
> /help/ <span> Try building your app first by running `alpas build` command.</span>
 
- **I get `Error: Got unexpected extra arguments` when running a console command.**
> /help/ <span> Double check the command name and arguments. Make sure that the command is actually
available by running `alpas help` first. </span>

- **`alpas help` doesn't list the commands that I'm looking for.**
> /help/ <span> Most probably because the service provider that actually provides those commands are not registered.
Check `ConsoleKernel.kt` file and make sure the service provider is actually registered. If it is not, register
it and then run the `alpas build`. The commands should be available now. </span>

- **`alpas migrate` doesn't run new migrations.**
> /help/ <span> Most probably because you haven't built the app after adding a new migration. Run `alpas build`
to build the app first and then run the migration. </span>

- **I'm stuck. I'm having other issues. I need to ask something else. I just want to hangout with cool Alpas devs.**
> /help/ <span>Please [join our Slack channel][alpas-slack] and say hi. </span>
>
- **I found a bug. I have a feature request.**
> /help/ <span> If you found a security related bug, please email us. For other issues and feature/enhancement
requests, please [open an issue][alpas-github-issue] on GitHub. </span>

</div>

[alpas-slack]: https://join.slack.com/t/alpasdev/shared_invite/enQtODcwMjE1MzMxODQ3LTJjZWMzOWE5MzBlYzIzMWQ2MTcxN2M2YjU3MTQ5ZDE4NjBmYjY1YTljOGIwYmJmYWFlYjc4YTcwMDFmZDIzNDE
[alpas-github-issue]: https://github.com/ashokgelal/alpas/issues/new
