package fr.kstars.forgenationsBattlepass.reward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor // <- Required for Jackson
@Getter
@Setter
public class Reward {
    private String name;
    private List<String> description;
    private String command;
    private int level;
    private boolean premium;
}