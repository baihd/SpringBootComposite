namespace java com.test.thrift.service.hello
service HelloWorldService {
  string sayHello(1:string username)
  string getRandom()
}