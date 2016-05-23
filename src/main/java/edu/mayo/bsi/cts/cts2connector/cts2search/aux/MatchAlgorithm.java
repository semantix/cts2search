package edu.mayo.bsi.cts.cts2connector.cts2search.aux;

/**
 * Created by dks02 on 5/23/16.
 */
public enum MatchAlgorithm
{
    EXACT{
        @Override
        public String toString() {
            return "";
        }
    },
    STARTSWITH{
        @Override
        public String toString() {
            return "startswith";
        }
    },
    ENDSWITH{
        @Override
        public String toString() {
            return "endswith";
        }
    },
    CONTAINS{
        @Override
        public String toString() {
            return "contains";
        }
    }
}
