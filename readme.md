# Pharmacy

## Setting up RabbitMQ

Docs: https://www.rabbitmq.com/docs/install-homebrew
<br>
### Quickstart:
For macos: `brew update` then `brew install rabbitmq`
<br>
To start a rabbitmq node in the background: `brew services start rabbitmq`
<br>
<strong>VERY IMPORTANT: Enable all feature flags with this command:</strong> `/opt/homebrew/sbin/rabbitmqctl enable_feature_flag all`
<br><br>
RabbitMQ should now be running and available to manage at http://localhost:15672
<br>
If it's not, you can find the Management UI URL with `brew info rabbitmq`
<br>
Default username/password: `guest/guest`