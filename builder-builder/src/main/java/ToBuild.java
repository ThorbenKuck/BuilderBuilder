import com.github.thorbenkuck.builder.annotations.InstantiationBuilder;
import com.github.thorbenkuck.builder.annotations.ValidateState;

@InstantiationBuilder
public class ToBuild {

  String shouldNotBeFilledLikeEver;
  String superDuperName;
  private String externalLameName;

  @ValidateState
  private void validate() {
    if (shouldNotBeFilledLikeEver != null) {
      throw new NullPointerException("shouldNotBeFilledLikeEver has to be null!");
    }
    if (superDuperName == null) {
      throw new NullPointerException("superDuperName cannot be null!");
    }
    if (externalLameName == null) {
      throw new NullPointerException("superDuperName cannot be null!");
    }
  }

  @Override
  public String toString() {
    return "ToBuild{" +
        "superDuperName=" + superDuperName +
        ", externalLameName=" + externalLameName +
        ", shouldNotBeFilledLikeEver=" + shouldNotBeFilledLikeEver +
        '}';
  }
}
