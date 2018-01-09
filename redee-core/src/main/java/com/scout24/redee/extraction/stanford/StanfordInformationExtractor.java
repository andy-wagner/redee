package com.scout24.redee.extraction.stanford;

import com.scout24.redee.extraction.Extraction;
import com.scout24.redee.extraction.InformationExtractor;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.MultiPatternMatcher;
import edu.stanford.nlp.ling.tokensregex.SequenceMatchResult;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * Created by dprawdzik on 11.07.17.
 */
public class StanfordInformationExtractor implements InformationExtractor {

    private StanfordCoreNLP pipeline;

    public StanfordInformationExtractor() {

        Properties properties = StringUtils.argsToProperties(
                new String[]{"-props", "stanford/StanfordCoreNlpDe.properties"});
        pipeline = new StanfordCoreNLP(properties);
    }

    public List<Extraction> extract(String content) {

        Annotation annotations = pipeline.process(content);
        List<Extraction> chunks = new ArrayList<>();

        for (CoreMap sentence : annotations.get(CoreAnnotations.SentencesAnnotation.class)) {

            List<CoreLabel> sentences = sentence.get(CoreAnnotations.TokensAnnotation.class);
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

            TokenSequencePattern pattern1 = TokenSequencePattern.compile("/the/ (/first/) /day/");
            TokenSequenceMatcher matcher1 = pattern1.getMatcher(tokens);
            matcher1.matches();
            matcher1.find();
            String matched = matcher1.group();
            System.out.println(matched);
            matched = matcher1.group();
            matcher1.get(0);
            System.out.println(matched);
            // List<CoreLabel> matchedNodes = matcher.groupNodes();

            TokenSequencePattern pattern = TokenSequencePattern.compile("([ner: I-PER]+) /war|ist/ /ein?/ []{0,6} /Künstler|Schauspieler/");
            TokenSequenceMatcher matcher = pattern.getMatcher(tokens);

            while (matcher.find()) {
                String matchedString = matcher.group();
                List<CoreMap> matchedTokens = matcher.groupNodes();
                System.out.println(matchedString);
            }


            for (CoreLabel token : tokens) {
                System.out.println(labelToString(token));
            }
            // Tree sentences = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);

            /*sentences.stream().filter(subTree -> subTree.label().value().equals("NP") ||
                    subTree.label().value().equals("MPN")).forEach(subtree -> {
                String image = subtree.yieldWords().stream().map(StringLabel::toString).collect(Collectors.joining(" "));


                    int start = subtree.yieldWords().get(0).beginPosition();
                    int end = subtree.yieldWords().get(subtree.yieldWords().size() - 1).endPosition();
                    Position position = new Position(Position.ParagraphType.VERBATIM.name(), start, end, "url");

                    Ref.Term term = new Ref.Term(image, image, subtree.nodeString(), position);
                    chunks.add(term);
            });*/
        }
        return chunks;
    }

    private String labelToString(CoreLabel token) {
        String word = token.get(CoreAnnotations.TextAnnotation.class);
        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
        String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
        return word + " - pos: " + pos + " - NE: " + ne;
    }
}
