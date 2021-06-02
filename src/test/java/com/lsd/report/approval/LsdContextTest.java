package com.lsd.report.approval;

import com.lsd.LsdContext;
import com.lsd.events.Markup;
import com.lsd.events.Message;
import com.lsd.events.NoteLeft;
import com.lsd.report.model.Participant;
import com.lsd.report.model.PopupContent;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;

import java.util.LinkedHashSet;
import java.util.List;

import static com.lsd.ParticipantType.*;
import static com.lsd.events.Message.ArrowType.*;
import static j2html.TagCreator.*;

class LsdContextTest {

    private final LsdContext lsdContext = LsdContext.getInstance();

    private LinkedHashSet<String> additionalIncludes = new LinkedHashSet<>(List.of(
            "tupadr3/font-awesome-5/hamburger",
            "tupadr3/font-awesome-5/heart"
    ));

    private final List<Participant> participants = List.of(
            ACTOR.called("A", "Arnie"),
            BOUNDARY.called("Unused participant"),
            DATABASE.called("B", "Badboy\\nDB")
    );

    private int counter = 0;

    @BeforeEach
    public void clearContext() {
        lsdContext.clear();
    }

    @Test
    void createsReportWithScenariosAndEvents() {
        lsdContext.addParticipants(participants);
        lsdContext.includeFiles(additionalIncludes);

        lsdContext.capture(Message.builder().id(incrementId()).from("A").to("B").label("Message 1").data("some data 1").arrowType(BI_DIRECTIONAL).build());
        lsdContext.capture(Message.builder().id(incrementId()).label("An interaction description that is long enough to need abbreviating").from("Beta").to("Gamma").data("β").arrowType(LOST).build());
        lsdContext.completeScenario("First scenario", "First scenario description");

        lsdContext.capture(Message.builder().id(incrementId()).label("Sending food <$hamburger{scale=0.4}>").from("A").to("B").colour("orange").arrowType(DOTTED_THIN).build());
        lsdContext.capture(new Markup("..."));
        lsdContext.capture(Message.builder().id(incrementId()).label("Sending a response").from("B").to("A").data("Thank You!").colour("red").arrowType(DOTTED).build());
        lsdContext.capture(new NoteLeft("Friends <$heart{scale=0.4,color=red}>"));
        lsdContext.completeScenario("Second scenario", p(
                text("A popup with a large amount of data that needs scrolling: "),
                a().withHref("#" + incrementId()).withText("click me!"),
                PopupContent.popupDiv(currentId(), "I am popup", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
        ).render());

        Approvals.verify(lsdContext.completeReport("Report 1").toFile());
    }

    private String incrementId() {
        return String.valueOf(++counter);
    }

    private String currentId() {
        return String.valueOf(counter);
    }
}