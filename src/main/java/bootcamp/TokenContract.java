package bootcamp;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import static net.corda.core.contracts.ContractsDSL.*;

import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
    public static String ID = "bootcamp.TokenContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        List<ContractState> inputs = tx.getInputStates();
        List<ContractState> outputs = tx.getOutputStates();
        CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);

        if (command.getValue() instanceof  Commands.Issue) {
            requireThat(requirements -> {
                //shape
                requirements.using("inputs must be 0", inputs.size() == 0);
                requirements.using("outputs must be 1", outputs.size() == 1);
                requirements.using("output is of type TokenState", outputs.get(0) instanceof TokenState);

                //content
                TokenState tokenState = (TokenState) outputs.get(0);
                requirements.using("amount must be greater thab 0", tokenState.getAmount() > 0);


                //required signatures
                requirements.using("Issuer has to be a required signer", command.getSigners().contains(tokenState.getIssuer().getOwningKey()));

                return null;
            });
        }

        if (inputs.size() != 0)
            throw new IllegalArgumentException("inputs have to be 0");


    }

    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}