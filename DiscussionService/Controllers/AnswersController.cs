using AutoMapper;
using DiscussionService.Filters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/answers")]
    public class AnswersController : ControllerBase
    {
        private IRepositoryWrapper _repositoryWrapper;
        private IMapper _mapper;
        public AnswersController(IRepositoryWrapper repositoryWrapper, IMapper mapper)
        {
            _repositoryWrapper = repositoryWrapper;
            _mapper = mapper;
        }

        [HttpGet]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetAllAnswers([FromQuery] AnswersQueryStringParameters queryStringParameters)
        {
            try
            {
                var answers = await _repositoryWrapper.AnswerRepository.GetAllAnswersAsync(queryStringParameters);

                var metadata = new
                {
                    answers.TotalCount,
                    answers.PageSize,
                    answers.CurrentPage,
                    answers.HasPrevious,
                    answers.HasNext
                };

                Response.Headers.Add("X-Pagination", JsonConvert.SerializeObject(metadata));
                var result = _mapper.Map<List<GetAnswerDto>>(answers);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }


        [HttpGet("{answerId}")]
        public async Task<IActionResult> GetAnswerById([FromRoute] Guid answerId)
        {
            try
            {
                var answer = await _repositoryWrapper.AnswerRepository.GetAnswerByIdAsync(answerId);
                var result = _mapper.Map<GetAnswerDto>(answer);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }


        [HttpPost]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> CreateAnswer([FromBody] CreateAnswerDto createAnswerDto)
        {
            try
            {
                var answer = _mapper.Map<Answer>(createAnswerDto);
                answer.Id = Guid.NewGuid();

                _repositoryWrapper.AnswerRepository.CreateAnswer(answer);
                await _repositoryWrapper.SaveAsync();

                return Ok();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPut("{answerId}")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> UpdateAnswer([FromRoute] Guid answerId, [FromBody] UpdateAnswerDto updateAnswerDto)
        {
            try
            {
                var newAnswer = _mapper.Map<Answer>(updateAnswerDto);
                var answer = await _repositoryWrapper.AnswerRepository.GetAnswerByIdAsync(answerId);

                if (answer == null)
                {
                    return NotFound();
                }

                answer.Content = newAnswer.Content;
                answer.ModifiedDate = DateTime.Now;
                _repositoryWrapper.AnswerRepository.UpdateAnswer(answer);
                await _repositoryWrapper.SaveAsync();
                return Ok();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpDelete("{answerId}")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> DeleteAnswer([FromRoute] Guid answerId)
        {
            try
            {
                var answer = await _repositoryWrapper.AnswerRepository.GetAnswerByIdAsync(answerId);

                if (answer == null)
                {
                    return NotFound();
                }

                // _repositoryWrapper.AnswerRepository.DeleteAnswer(answer);
                answer.Removed = true;
                answer.RemovedBy = "N/a";
                _repositoryWrapper.AnswerRepository.UpdateAnswer(answer);
                await _repositoryWrapper.SaveAsync();
                return Ok();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }
    }
}