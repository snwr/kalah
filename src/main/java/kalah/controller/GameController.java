package kalah.controller;

import kalah.model.Board;
import kalah.model.Move;
import kalah.model.ValidationResult;
import kalah.service.GameService;
import kalah.validator.InputValidatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Main controller for game operations.
 */
@RestController()
@RequestMapping("/api")
public class GameController {

	@Autowired
	private GameService gameService;
	
	@Autowired
	private InputValidatorChain inputValidatorChain;

	/**
	 * Creates a new game board.
	 * @return ResponseEntity wrapping Board.
	 */
	@RequestMapping(value="/newBoard", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Board> newBoard() {
		Board board = gameService.createNewBoard();

		return ResponseEntity
						.status(HttpStatus.OK)
						.body(board);
	}

	/**
	 * Returns specific board by id.
	 * @param boardId
	 * @return ResponseEntity wrapping Board.
	 */
	@RequestMapping(value="/currentBoard", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<Board> currentBoard(@RequestBody String boardId) {
		return ResponseEntity
						.status(HttpStatus.OK)
						.body(gameService.getBoard(boardId));
	}

	/**
	 * Applies move on a specific board and returns operation result.
	 * @param move
	 * @return ValidationResult
	 */
	@RequestMapping(value="/play", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity play(@RequestBody Move move) {
		
		ValidationResult validationResult = inputValidatorChain.validateInput(gameService.getBoard(move.getBoardId()), move);
		if(!validationResult.getMessage().equals(InputValidatorChain.VALID_MSG)) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(validationResult);
		}
		
		Board afterMove = gameService.play(move);
		if(afterMove==null) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(afterMove);
		
	}	
}
