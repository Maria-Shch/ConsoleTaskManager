# Task manager :date:
Console manager for managing your tasks.  

## Description :memo:
The application has a console interface in Russian:

![image](https://user-images.githubusercontent.com/62648024/162632691-e6c84c74-c7fa-458d-9278-032e3e5c5c4d.png)

For each task, the user can set the following parameters:
- Title
- Description
- Notification date
- Contact details


![image](https://user-images.githubusercontent.com/62648024/162633045-50796b2e-7e8d-4a54-8ab7-15cd43eb93fe.png)

### User notification :bell:
At the user-specified notification time, the following window is displayed:

![image](https://user-images.githubusercontent.com/62648024/162633751-52a3135f-12b1-4ed1-9a3d-90e8b861c99c.png)

The user can complete or postpone the task.

![image](https://user-images.githubusercontent.com/62648024/162633871-af449418-5b64-4f3e-ae95-769b4aaf3b9f.png)

![image](https://user-images.githubusercontent.com/62648024/162633890-b91d591a-7862-4455-baf1-1e59553f96a5.png)

### Data storage 	:cd:
Between application launches, all data about tasks is stored locally in files. Storage of tasks in XML and JSON formats is available. The choice of file format depends on the settings of the `taskManager.properties` file:

![image](https://user-images.githubusercontent.com/62648024/162634319-7f2fea3b-f19d-4a97-b243-33b9803e4ab3.png) ![image](https://user-images.githubusercontent.com/62648024/162634339-a48e4af9-c844-4250-8222-4d72e61ddab9.png)

>**Warning!** The application does not support data synchronization between data files different formats.


## Dependencies :link:
This application is written in **`Java`** using these libraries and tools:
- [Spring Framework 5.2.15](https://spring.io/)
- [json-simple 1.1.1](https://github.com/fangyidong/json-simple)
- [jackson-databind 2.13.0](https://github.com/FasterXML/jackson-databind)
- [log4j 1.2.12](https://logging.apache.org/log4j/2.x/index.html)
- [Swing](https://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html)

## Built With :round_pushpin:
- [Maven](https://maven.apache.org/) - Dependency Management
