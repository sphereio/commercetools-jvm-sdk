package io.sphere.sdk.states;

import io.sphere.sdk.client.TestClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.states.commands.StateCreateCommand;
import io.sphere.sdk.states.commands.StateDeleteCommand;
import io.sphere.sdk.states.commands.StateUpdateCommand;
import io.sphere.sdk.states.commands.updateactions.SetTransitions;
import io.sphere.sdk.states.queries.StateQuery;
import io.sphere.sdk.utils.SetUtils;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.sphere.sdk.test.SphereTestUtils.consumerToFunction;
import static io.sphere.sdk.test.SphereTestUtils.randomKey;
import static io.sphere.sdk.utils.SetUtils.asSet;
import static java.util.Locale.ENGLISH;

public class StateFixtures {
    private StateFixtures() {
    }

    public static State createStateByKey(final TestClient client, final String key) {
        final StateDraft stateDraft = createStateDraft(key);
        return client.execute(StateCreateCommand.of(stateDraft));
    }

    private static StateDraft createStateDraft(final String key) {
        return StateDraft.of(key, StateType.LINE_ITEM_STATE)
                .withDescription(LocalizedString.of(ENGLISH, "description"))
                .withName(LocalizedString.of(ENGLISH, "name"))
                .withInitial(Boolean.TRUE);
    }

    public static void cleanUpByKey(final TestClient client, final String key) {
        client.execute(StateQuery.of().byKey(key)).head().ifPresent(state -> client.execute(StateDeleteCommand.of(state)));
    }

    public static void withState(final TestClient client, final Consumer<State> consumer) {
        withUpdateableState(client, consumerToFunction(consumer));
    }

    /**
     * Provides states where the first one is an initial state and has a transition to the second one.
     * The states may reused and won't be deleted.
     * @param client sphere test client
     * @param consumer consumer which uses the two states
     */
    public static void withStandardStates(final TestClient client, final BiConsumer<State, State> consumer) {
        final String keyA = "Initial";//given from SPHERE.IO backend
        final String keyB = StateFixtures.class + "_B";
        final State stateB = client.execute(StateQuery.of().byKey(keyB)).head().orElseGet(() -> createStateByKey(client, keyB));
        final State stateA = client.execute(StateQuery.of().byKey(keyA)).head()
                .map(initialState -> {
                    final Optional<Set<Reference<State>>> transitionOptional = Optional.ofNullable(initialState.getTransitions());
                    final Boolean initialCanTransistToStateB = transitionOptional.map(transitions -> transitions.contains(stateB.toReference())).orElse(false);
                    final Set<Reference<State>> transitions = transitionOptional.map(trans -> SetUtils.setOf(stateB.toReference(), trans)).orElse(asSet(stateB.toReference()));
                    final SetTransitions action = SetTransitions.of(transitions);
                    final StateUpdateCommand updateCommand = StateUpdateCommand.of(initialState, action);
                    return initialCanTransistToStateB ? initialState : client.execute(updateCommand);
                })
                .get();
        consumer.accept(stateA, stateB);
    }

    public static void withUpdateableState(final TestClient client, final Function<State, State> f) {
        final State state = createStateByKey(client, randomKey());
        final State updatedState = f.apply(state);
        client.execute(StateDeleteCommand.of(updatedState));
    }
}
