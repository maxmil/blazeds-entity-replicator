## Whats all this about?

Blaze DS makes it very easy to pass rich object graphs between server and client. This is great.

However when working with ORM's (Hibernate for example) the relations that exist between entities that you have defined in server can cause Blaze DS to serialize whole object graphs which at best is inefficient and at its worst can result in you serializing your whole database and sending it to your flex client without realizing it.

What's more Blaze serialization can cause problems with lazily loaded entities.

## The solution

There are already several solutions to this problem. [[DpHibernate|http://code.google.com/p/dphibernate/] and [[Gilead|http://noon.gilead.free.fr/gilead/]] being two very complete solutions. However if you need something a bit simpler than this may be for you.

It consists of a **annotation** that you can add to methods that return results to Blaze DS. This annotation can control which parts of the object graph you are returning should be serialized and sent to the flex client.

## How to use it

There are two ways to configure it: 

1. **fetchEager** (true by default): When false any collection that has not been initialized by Hibernate will not be included in serialization 

2. **excludes** (empty by default): A list of properties that should be excluded from serialization. Each property is defined by its containing class and the name of the property.

## Examples


For a method that returns an instance of Foo here are a few examples of with the annotation

1) This will return an instance of Foo with any non initialized lazily initialized collections set to null.

```@ReplicateResult(fetchEager = false)
```


2) This will return an instance of Foo with any property named *bar* set to null.

```@ReplicateResult(exclude = {@ReplicateProperty(clazz = Foo.class, property="bar")})
```

3) The same would apply if Foo contained a collection named *bars"

```@ReplicateResult(exclude = {@ReplicateProperty(clazz = Foo.class, property="bars")})
```


4) Any point in the object graph can be excluded. For example if the returned instance of Foo contains an instance of Bar which contains an property named *baz* then this will return everything excluding *baz*.

```@ReplicateResult(exclude = {@ReplicateProperty(clazz = Bar.class, property="baz")})
```


5) The excluded properties are a list so you can specify as many as you want.

```@ReplicateResult(exclude = {
@ReplicateProperty(clazz = Foo.class, property="bar"),
@ReplicateProperty(clazz = Foo.class, property="baz"),
@ReplicateProperty(clazz = Bar.class, property="bars"),
@ReplicateProperty(clazz = Bar.class, property="baz"),
@ReplicateProperty(clazz = Baz.class, property="foos")})
```


6) The fetchEager property and excludes can be used together.

```@ReplicateResult(fetchEager = false, exclude = {@ReplicateProperty(clazz = Foo.class, property="bar")})
```


## Download

You can download the latest release from the "Download" link on github.


## How to build it

This project uses Maven 2 as its build system.

To build the project, from the root directory issue the command:

mvn clean install


## How it works

Internally it uses beanlib to replicate the object returned by the method. The main work is really done by beanlib, this project is just a thin wrapper on top.


## Dependencies

This project will only be useful if you are using BlazeDS and Hibernate (with or without spring).

To see the dependencies of the project check pom.xml

