public class Test {

  public static void main(String[] args) {
    ToBuild build = ToBuildBuilder.newInstance()
        .withSuperDuperName("SuperDuper")
        .withExternalLameName("External")
        .withShouldNotBeFilledLikeEver(null)
        .build();

    System.out.println(build);
  }
}
