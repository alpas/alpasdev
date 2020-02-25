- [Prerequisites](#prerequsities)
- [Preparing the server - First Run](#first-run)
- [Subsequent Deployments](#subsequent-deployments)
- [Troubleshooting](#troubleshooting)

You can deploy and serve an Alpas web app behind a proxy server by creating a fat jar using `alpas jar` 
command and then copying the jar over to the root of your server.

<a name="prerequsities"></a>
### [Prerequisites](#prerequsities)

You can use any Linux server for deploying but we recommend Ubuntu 18.04 LTS or higher.
Here are the pre-requisites for your deployment server.

<div class="sublist">

* Ubuntu 18.04 or higher
* Nginx
* JRE 9 or higher
* root access

</div>

You can use a tool like [Cleaver](https://getcleaver.com) to quickly provision a server and creating a new site
with SSL certificates ready to go.

<a name="first-run"></a>
### [Preparing the server - First Run](#first-run)

>/alert/<span>The following steps assume that your domain name is `example.com`, your app name is `example`, and
>your web root folder for this app is under `/home/cleaver/example.com`. Make changes accordingly for your web app.

<div class="ordered-list">

1. Install JRE: `sudo apt install openjdk-11-jre-headless` <span class="clipboard" data-clipboard-text='sudo apt install openjdk-11-jre-headless'></span>
2. Create a service file `/etc/systemd/system/example.com.service` and paste the following contents:

<span class="line-numbers" data-start="1" data-file="/etc/systemd/system/example.com.service">

```properties

[Unit]
Description=example.com
After=network.target

[Service]
ExecStart=/usr/bin/java -jar example.jar
Restart=always
User=cleaver
Group=cleaver
Environment=PATH=/usr/bin:/usr/local/bin
WorkingDirectory=/home/cleaver/example.com

[Install]
WantedBy=multi-user.target

```

</span>

3. Create a nginx config file for your domain `/etc/nginx/sites-available/example.com` and paste the following:

<span class="line-numbers" data-start="1" data-file="/etc/nginx/sites-available/example.com">

```nginx

include cleaver-conf/example.com/header/*.conf;

map $http_upgrade $connection_upgrade {
    default upgrade;
    ''   '';
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;

    include /etc/nginx/h5bp/directive-only/ssl.conf;
    include /etc/nginx/h5bp/directive-only/ssl-stapling.conf;

    ssl_certificate       /etc/nginx/ssl/example.com/fullchain.pem;
    ssl_certificate_key   /etc/nginx/ssl/example.com/privkey.pem;
    ssl_dhparam /etc/nginx/dhparams.pem;
    server_name example.com;

    charset utf-8;

    include cleaver-conf/example.com/*.conf;

    client_max_body_size 0;
    client_header_buffer_size 50M;
    proxy_read_timeout     1200;
    proxy_connect_timeout  240;

    access_log off;
    location = /favicon.ico { access_log off; log_not_found off; }
    location = /robots.txt  { access_log off; log_not_found off; }
    error_log /var/log/nginx/example.com-error.log error;

    location / {
        proxy_pass              http://localhost:9999;
        proxy_http_version      1.1;
        proxy_set_header        X-Forwarded-For $remote_addr;
        proxy_set_header        X-Forwarded-Proto      $scheme;
        proxy_set_header        X-Forwarded-Host       $http_host;
        proxy_set_header        Upgrade                $http_upgrade;
        proxy_set_header        Connection             $connection_upgrade;
    }

    root /home/cleaver/example.com/current;
    location ^~ /.well-known/acme-challenge/ {
        allow all;
    }
}

```

</span>

Make note of the port number **9999** above in `proxy_pass  http://localhost:9999;`. You can change it to some random
port number but you'll need this port number value in your `.env` file later.

4. Check to see if `/etc/nginx/sites-enabled/example.com` exists.

If not, you need to create a link pointing to the above config file we created by running: `cd /etc/nginx/sites-enabled && ln -s ../sites-available/example.com .`

5. Now you need to these files from your local project folder over to remote `/home/cleaver/example.com` folder:

<div class="sublist">

* app_log_config.xml
* console_log.config.xml
* .env
* example.jar

</div>

You can use `scp` command to copy from your local machine to remote machine. This is how we'd copy an `example.jar` file:

```bash

scp -i ~/.ssh/cleaver/<private-ssh-key> ./example.jar cleaver@<server-ip>:/home/cleaver/example.com/

```

`<private-ssh-key>` is the private ssh key on your local machine that allows you to connect to the remote server.

6. Open `/home/cleaver/example.com/.env` file on the remote server and make few changes:

<span class="line-numbers" data-start="2" data-file="/home/cleaver/example.com/.env">


```toml

APP_PORT=<port-address-from-step-3>
APP_LEVEL=prod
APP_URL=example.com

```

</span>

7. Let's reload the service file: `sudo systemctl daemon-reload`,
8. Let's test nginx config to make sure everything is good: `sudo service nginx configtest`. If this says **[ FAIL ]**, then
something is wrong in your nginx config file. Don't proceed before fixing it.
9. If step 8 says **[ OK ]**, then run `sudo service nginx reload`.
10. Let's restart the actual web app now `sudo service thatappshow.com restart`.

Give it a couple of seconds and your site should now be available at `https://example.com`.

</div>

<a name="subsequent-deployments"></a>
### [Subsequent Deployments](#subsequent-deployments)

The above steps are only for initial run. Once your server is setup, for the subsequent deployments all you
have to do is build your jar file locally, copy it over, and then restart the web app service.

<div class="ordered-list">

1. To build the jar: `./alpas jar`.
2. To copy the jar: `scp -i ~/.ssh/cleaver/<private-ssh-key> ./example.jar cleaver@<server-ip>:/home/cleaver/example.com/`.
3. To restart the server (from within the remote server and as a root user): `sudo service thatappshow.com restart`.

</div>

<a name="troubleshooting"></a>
### [Troubleshooting](#troubleshooting)

If you run into any issue, you can check the log files. Most of the times it is because you didn't use the correct
port number and somehow mis-configured your nginx config file. Run the following command to check the log messages 
of your as they come in:

```bash

journalctl -f -u example.com

```
