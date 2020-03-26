# The Builder Builder

What is the builder builder? This project is more an example of how we can use Annotation Processing to reduce the use of reflections than a concrete api.

If you did not come here from the presentation: I am sorry for the missing context.

## The Modules

We here have a Maven project, that emulates a project, that uses a framework.

The folder `builder-builder` contains the builder-builder module. This is our "PerformanceTest-Project" where we can test changes and use cases.

The folder `builder-buider-annotations` contains only the meta-data of this project. Like javax (which we also utilize). Our custom annotations can be found here.

The last folder `builder-builder-processor` obviously contains the annotation processor. If we change anything here, we **have** to run `mvn clean install`, otherwise these changes are not found in our test-project.

Testing here does not require to establish the architecture. This is already done. You can change and try around as much as you like.

## Testing the Builder Builder

The idea is, that we have an annotation processor, that creates a builder according to the Builder-Pattern for any class.

To show of, that we can reduce the use of reflections, but not totally eliminate it in the current ecosystem, this example supports field injection with private fields.

If you want to play around, you obviously have to check this project out.

`git clone https://github.com/ThorbenKuck/BuilderBuilder.git`

**Warning:** Every time you recompile, something might be broken! Obviously, because this is what we want to try and do.

### 0 - (OPTIONAL) Setup your IDE to also find the annotation processor

If you want to see errors in your IDE upon saving the file, you have to set it up accordingly. Some IDEs do not natively seek out for annotation processors to be included in their compiling process. To allow this, you may have to enable it.

This would be easier, if this example would be an external framework. However, google for "<my-ide> annotation processor" to find out how to setup your IDE. Eclipse has the best integration of that in my opinion.

Support for compiling a list is very much appreciated.

### 1 - See It Happening

Change into the builder-builder project. This project contains an example.

In this project run:

```mvn clean install```

And you are good to go! In this project, you can find 3 classes. `ToBuild`, `Test` and `PerformanceTest`. The last one is not required at all. `ToBuild` is the class we want to create a builder for. `Test` is a main class.

Change to the folder:

```target/generated-sources/annotations/```

In here you can find the builder. Default wise this is named `ToBuildBuilder`

### 2 - Play around a little

Add some annotations to the `ToBuild` class. Add some of these annotations to the fields of the class:

- @IgnoreField
- @SetterName
- @javax.annotation.Nullable

If your ide is not set up correctly, run this again after each change:

```mvn clean install```

You also might be covered by just running the `Test` class, but this is depending on your IDE

Now look at the generated Builder. How did it change? Also try to make some fields private and look how this works.

### 3 - Validation

Try to add the following code:

```java
import com.github.thorbenkuck.builder.annotations.ValidateState;
import com.github.thorbenkuck.builder.annotations.processor.Validate;

// .......

@ValidateState
void validate() {
    if (shouldNotBeFilledLikeEver != null) {
      throw new NullPointerException("shouldNotBeFilledLikeEver has to be null!");
    }
    Validate.notNull(superDuperName, "superDuperName cannot be null!");
}
```

Now have a look into what happened with the Builder. How is this method invoked? And what happens, if you change the visibility to private?

### 4 - Understanding what is happening

This is something i cannot help you with. Go into the builder-builder-processor project and look at the class `CreateBuilderProcessor`. This not overly designed, so don't expect something extravagant. It is kept as simple as possible.

If you really want to get into what is happening, [there are thousands of good sources](https://www.google.com/search?q=annotation+processing+java&oq=annotation+processing+java).

A good staring point in german in [this jax article](https://jax.de/blog/core-java-jvm-languages/java-annotation-processing-das-koennte-auch-ein-computer-erledigen/).

For an english entry point, have a look at [this blog post](http://hannesdorfmann.com/annotation-processing/annotationprocessing101).

### 5 - Enhance and Expand

If you want to be challenged a bit, try to enhance the project. Change to the project builder-builder-processor and create an `UpdateBuilderProcessor`, that processes the `UpdateBuilder` annotation.

This should create a builder, that allows for updating the class.

Fork this project, try to implement this and if you feel extra good, create a merge request back to this project.

Try to add new annotations and implement them.