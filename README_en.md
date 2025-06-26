# ProgramBook

[中文 README](README.md)

This project is a WeChat mini program bookstore for programmers (ProgramBook). The frontend uses the Uniapp framework and the backend uses SpringBoot.

Modify the port, Redis, MongoDB and MySQL addresses in `application.yml`, and update the Alibaba Cloud OSS settings in `com.book.controller.user.UserController`:

![Snipaste_2023-06-04_10-57-07](/images/Snipaste_2023-06-04_10-57-07.png)

Import the MongoDB and MySQL data, then start the service.

For the frontend, open the project in HBuilderX and change the `HOST_URL` IP in `/utils/api/api.js` to your own before running it in the mini program simulator.

![image-20230404181954029](/images/image-20230404181954029.png)

## Project structure
- `afterEnd/ProgramBook` backend code
- `frontEnd/book` frontend code

## Features
- WeChat login with tokens stored in Redis
- Book search, categories and ranking list
- Reading progress sync and reading time statistics
- Personal bookshelf, comments, daily sign-in and notifications
- User avatars stored in Alibaba Cloud OSS

## Screenshots

![image-20230404182232643](/images/image-20230404182232643.png)

![image-20230404182401471](/images/image-20230404182401471.png)

![image-20230404182610721](/images/image-20230404182610721.png)

![image-20230404182651488](/images/image-20230404182651488.png)

![image-20230404182802737](/images/image-20230404182802737.png)

## Contributing

Maintained by the author.

## License

GPL
