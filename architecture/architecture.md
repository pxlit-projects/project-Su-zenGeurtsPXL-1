# Architecture
![Architecture](https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1/blob/main/architecture/architecture.drawio.svg)

### Synchronous communications
###### Post Service
- When fetching a post by id with its reviews, the reviews are fetched by id from the `Review Service`.
- When fetching a post by id with its comments, the comments are fetched by id from the `Comment Service`.

###### Review Service
- When reviewing a post, the state of the post is checked by fetching the post from the `Post Service`.

###### Comment Service
- When commenting on a post, the state of the post is checked by fetching the post from the `Post Service`.

### Asynchronous communications
###### Review Service
- When reviewing a post, a message is sent to the event bus for the `Post Service` to read.

###### Post Service
- When listening to the event bus for messages from the `Review Service`, a notification is added per message.
