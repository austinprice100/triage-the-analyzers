package team.projectpulse.team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.projectpulse.section.Section;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TeamSpecsTest {

    @Mock
    Root<Team> root;

    @Mock
    CriteriaQuery<?> query;

    @Mock
    CriteriaBuilder criteriaBuilder;

    @Mock
    Path<String> teamNamePath;

    @Mock
    Path<Section> sectionPath;

    @Mock
    Path<String> sectionNamePath;

    @Mock
    Expression<String> loweredPath;

    private Locale originalLocale;

    @BeforeEach
    void setUp() {
        this.originalLocale = Locale.getDefault();
        // Turkish lowercases "I" to the dotless "ı", which exposes case conversions that use the default locale.
        Locale.setDefault(Locale.forLanguageTag("tr"));
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(this.originalLocale);
    }

    @Test
    void testContainsTeamNameLowercasesIndependentOfDefaultLocale() {
        given(this.root.<String>get("teamName")).willReturn(this.teamNamePath);
        given(this.criteriaBuilder.lower(this.teamNamePath)).willReturn(this.loweredPath);

        TeamSpecs.containsTeamName("IVY").toPredicate(this.root, this.query, this.criteriaBuilder);

        ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
        verify(this.criteriaBuilder).like(eq(this.loweredPath), pattern.capture());
        assertThat(pattern.getValue()).isEqualTo("%ivy%");
    }

    @Test
    void testHasSectionNameLowercasesIndependentOfDefaultLocale() {
        given(this.root.<Section>get("section")).willReturn(this.sectionPath);
        given(this.sectionPath.<String>get("sectionName")).willReturn(this.sectionNamePath);
        given(this.criteriaBuilder.lower(this.sectionNamePath)).willReturn(this.loweredPath);

        TeamSpecs.hasSectionName("SECTION I").toPredicate(this.root, this.query, this.criteriaBuilder);

        ArgumentCaptor<Object> value = ArgumentCaptor.forClass(Object.class);
        verify(this.criteriaBuilder).equal(eq(this.loweredPath), value.capture());
        assertThat(value.getValue()).isEqualTo("section i");
    }

}
