package test;

public class Test {

  public static void main(String[] args) {
    ToBuild build = ToBuildBuilder.newInstance()
        .withSuperDuperName("SuperDuper")
        .withShouldNotBeFilledLikeEver("Filled regardless")
        .withExternalLameName("Is not lame")
        .build();

    System.out.println(build);
  }

}
