package test;

import com.github.thorbenkuck.builder.annotations.InstantiationBuilder;

// Try to change my name
@InstantiationBuilder
public class ToBuild {

  // Prevent me from being available in the Builder!
  String shouldNotBeFilledLikeEver;
  // Try and make me private
  String superDuperName;
  // Try to change my name.. Don't want to let anybody know i am lame!
  String externalLameName;

  // Not required, but prohibits access from outside this package
  ToBuild() {}

  // I am here, so you can see your results later
  @Override
  public String toString() {
    return "ToBuild{" +
        "superDuperName=" + superDuperName +
        ", externalLameName=" + externalLameName +
        ", shouldNotBeFilledLikeEver=" + shouldNotBeFilledLikeEver +
        '}';
  }
}
