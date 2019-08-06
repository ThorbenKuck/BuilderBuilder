import com.github.thorbenkuck.builder.annotations.InstantiationBuilder;
import com.github.thorbenkuck.builder.annotations.IgnoreField;
import com.github.thorbenkuck.builder.annotations.SetterName;
import com.github.thorbenkuck.builder.annotations.ValidateState;

@InstantiationBuilder(builderName = "MyCustomBuilderName")
public class ToBuild {

    String superDuperName;
    @SetterName("withExternalName")
    String externalLameName;
    @IgnoreField
    String shouldNotBeFilledLikeEver;

    @ValidateState
    void validate() {
        if(shouldNotBeFilledLikeEver != null) {
            throw new NullPointerException("shouldNotBeFilledLikeEver has to be null!");
        }
        if(superDuperName == null) {
            throw new NullPointerException("superDuperName cannot be null!");
        }
        if(externalLameName == null) {
            throw new NullPointerException("superDuperName cannot be null!");
        }
    }

    @Override
    public String toString() {
        return "ToBuild{" +
                "superDuperName=" + superDuperName  +
                ", externalLameName=" + externalLameName +
                ", shouldNotBeFilledLikeEver=" + shouldNotBeFilledLikeEver +
                '}';
    }
}
