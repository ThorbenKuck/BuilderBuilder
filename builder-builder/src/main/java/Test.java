public class Test {

    public static void main(String[] args) {
        ToBuild build = MyCustomBuilderName.newInstance()
                .withSuperDuperName("SuperDuper")
                .withExternalName("External")
                .build();

        System.out.println(build);
    }

}
